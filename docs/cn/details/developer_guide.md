# AppActive 开发指南

---

## 一、数据面
### 1.1 网关（Gateway）：Nginx

**前置条件**

- 需要你的网关基于 nginx 实现，并且具备运行 lua 的能力

**改造步骤**

将 `appactive-gateway` 下的目录文件作为你的网关配置文件。

特别的，你需要按照如下模版来渲染 nginx 每一个域名 文件：

```
server {
    listen 80 ;

    server_name %VAR_DOMAIN% %VAR_CENTER_DOMAIN% %VAR_UNIT_DOMAIN%;

    include srv.cfg;

    location %VAR_URI% {
        set $app %VAR_APP_ID%;
        set $unit_type test1122;
        set $rule_id 459236fc-ed71-4bc4-b46c-69fc60d31f18;
        set $router_rule ${rule_id}_${unit_type};
        set $unit_key '';
        set $cell_key '';
        set $unit_enable %VAR_UNIT_ENABLE%;
        include loc.cfg;
    }
}

upstream %VAR_APP_ID%_%UNIT_FLAG_1%_default {
    %VAR_BACKEND_IP_LIST%"
}

upstream %VAR_APP_ID%_%UNIT_FLAG_N%_default {
    %VAR_BACKEND_IP_LIST%"
}

```

其中：

- VAR_DOMAIN: 终端用户直接使用的域名，必选。另外两个单元子域名可选
- VAR_URI: 具体的URI
- VAR_APP_ID: 域名+URI构成的唯一前端。需要将 `.` 换成 `_` ，`/` 换成 `@`
- VAR_UNIT_ENABLE: 当前URI是否以多活方式寻址，为1则是，为0则否（直接走本单元）
- UNIT_FLAG_N: 每个单元的单元标
- VAR_BACKEND_IP_LIST: 对应单元的后端应用IP

### 1.2 微服务（RPC）：Dubbo
**前置条件**

- 需要你的应用服务基于 Java 实现，并且以 Dubbo 实现服务调用

#### 入口应用

入口应用负责从流量中提取路由标，并设置到上下文中

**改造步骤**

1. 引入 maven 依赖

```
<dependency>
    <groupId>com.alicloud.msha</groupId>
    <artifactId>client-bridge-servlet</artifactId>
    <version>1.2-SNAPSHOT</version>
</dependency>
```

2. 引入 filter，以 Spring 为例

```java
@Configuration
public class WebConfig {
    @Bean
    public FilterRegistrationBean<RequestFilter> appActiveFilter() {
        FilterRegistrationBean<RequestFilter> filterRegistrationBean = new FilterRegistrationBean<>();
        RequestFilter reqResFilter = new RequestFilter();
        filterRegistrationBean.setFilter(reqResFilter);
        filterRegistrationBean.addUrlPatterns("/*");
        return filterRegistrationBean;
    }
}
```
3. 当请求到来时，可以在应用中调用 `AppContextClient.getRouteId();` 来获取路由ID

#### 所有应用
**改造步骤**

1. 在 provider 和 consumer 都引入 maven 依赖

```
<dependency>
    <groupId>com.alicloud.msha</groupId>
    <artifactId>client-bridge-rpc-apache-dubbo2</artifactId>
    <version>1.2-SNAPSHOT</version>
</dependency>
<dependency>
    <groupId>com.alicloud.msha</groupId>
    <artifactId>client-bridge-rpc-apache-dubbo2-metainfo</artifactId>
    <version>1.2-SNAPSHOT</version>
</dependency>
<dependency>
    <groupId>com.alicloud.msha</groupId>
    <artifactId>client-spi-metainfo</artifactId>
    <version>1.2-SNAPSHOT</version>
</dependency>
<dependency>
    <groupId>com.alicloud.msha</groupId>
    <artifactId>client-rule</artifactId>
    <version>1.2-SNAPSHOT</version>
</dependency>
```

2. 在 provider 中加入属性，如果是注解形式如下

```
package io.appactive.demo.product.service;

import io.appactive.demo.common.entity.Product;
import io.appactive.demo.common.entity.ResultHolder;
import io.appactive.demo.common.service.ProductServiceUnit;
import io.appactive.demo.product.repository.ProductRepository;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

@DubboService(version = "1.0.0", group = "appactive", parameters = {"rsActive","unit","routeIndex","0"})
public class ProductServiceUnitImpl implements ProductServiceUnit {

    @Value("${io.appactive.demo.unit}")
    private String unit;

    @Autowired
    ProductRepository productRepository;

    @Override
    public ResultHolder<Product> detail(String rId, String pId) {
        // unit
        System.out.println("detail: " + rId + " : " + pId);
        return new ResultHolder<>(productRepository.findById(pId).orElse(new Product()));
    }
}

```

核心是加上注解
` parameters = {"rsActive","unit","routeIndex","0"} `
rsActive 为 unit 表明这是一个单元服务，routeIndex 为 0 表明路由ID是第 0 个参数。
rsActive 的 候选 value 有:

- normal: 普通服务
- unit: 单元服务
- center: 中心服务

对于单元服务，支持显式调用和隐式调用。其中显式调用需要如上改造方法签名，并用 routeIndex 注明路由ID参数位置。

而隐式调用则不需要改造方法签名，仅需要在实际调用方法前手工设置路由ID `AppContextClient.setUnitContext("12");`

### 1.3 数据库（DB）：Mysql

**前置条件**

- 需要你的数据库是 MySQL（5.x） ，且最终写库的应用基于 Java 实现

**改造步骤**

1. 引入 maven 依赖

```
<dependency>
    <groupId>com.alicloud.msha</groupId>
    <artifactId>client-spi-metainfo</artifactId>
    <version>1.2-SNAPSHOT</version>
</dependency>
 <dependency>
    <groupId>com.alicloud.msha</groupId>
    <artifactId>client-bridge-db-mysql</artifactId>
    <version>1.2-SNAPSHOT</version>
</dependency>
```
2. 数据库连接加上参数，如
`jdbc:mysql://mysql:3306/product?characterEncoding=utf8&useSSL=false&serverTimezone=GMT&activeInstanceId=mysql&activeDbName=product`。其中：
	- activeInstanceId: 数据库实例ID
	- activeDbName: 数据库名字
	- activePort: 数据库实例端口，3306可以不填

3. 更换 driver，如: `spring.datasource.driver-class-name=io.appactive.db.mysql.driver.Driver`

### 1.4 基本配置
凡是依赖 `appactive-java-api` 模块的应用，启动时候都要配置参数 `-Dappactive.path=/path/to/path-address`。
path-address的内容为：

```
{
    "appactive.machineRulePath":"/app/data/machine.json",
    "appactive.dataScopeRuleDirectoryPath":"/app/data",
    "appactive.forbiddenRulePath":"/app/data/forbiddenRule.json",
    "appactive.trafficRulePath":"/app/data/idUnitMapping.json",
    "appactive.transformerRulePath":"/app/data/idTransformer.json",
}

```
其中

- appactive.forbiddenRulePath: 描述禁写哪些路由标
- appactive.transformerRulePath: 描述如何解析路由标
- appactive.trafficRulePath: 描述路由标和单元的映射关系
- appactive.machineRulePath: 描述当前机器的归属单元
- appactive.dataScopeRuleDirectoryPath: 存放数据库的属性文件，一个数据库一个文件，文件命名为：activeInstanceId-activeDbName 或者 activeInstanceId-activeDbName-activePort

## 二、管控面

在应用部署完成后要进行基线推送，在希望调整流量时进行切流。核心是规则的构造和推送，这里重点将几个规则进行说明。

- appactive.transformerRulePath，举例：

```
{
  "id": "userIdBetween",
  "mod": "10000"
}
```
说明，提取到路由标后按照10000取模，作为最终路由标。


- appactive.trafficRulePath，举例：

```
{
  "itemType": "UnitRuleItem",
  "items": [
    {
      "name": "unit",
      "conditions": [
        {
          "@userIdBetween": [
            "0~1999"
          ]
        }
      ]
    },
    {
      "name": "center",
      "conditions": [
        {
          "@userIdBetween": [
            "2000~9999"
          ]
        }
      ]
    }
  ]
}

```
说明 按 10000 取模后在 0～1999 范围内的路由标应该被路由到 unit；

按 10000 取模后在 2000～9999 范围内的路由标应该被路由到 center；

- appactive.machineRulePath，举例：

```
{"unitFlag":"unit"}

```
说明当前应用部署在 unit

- appactive.forbiddenRulePath，举例：

假设我们希望将 2000~2999 从 unit 划分到 center，则新的appactive.trafficRulePath如下

```
{
  "itemType": "UnitRuleItem",
  "items": [
    {
      "name": "unit",
      "conditions": [
        {
          "@userIdBetween": [
            "0~2999"
          ]
        }
      ]
    },
    {
      "name": "center",
      "conditions": [
        {
          "@userIdBetween": [
            "3000~9999"
          ]
        }
      ]
    }
  ]
}

```

对应的appactive.forbiddenRulePath 为

```
{
  "itemType": "ForbiddenRuleItem",
  "items": [
    {
      "name": "between",
      "conditions": [
        {
          "@userIdBetween": [
            "2000~2999"
          ]
        }
      ]
    }
  ]
}


```
