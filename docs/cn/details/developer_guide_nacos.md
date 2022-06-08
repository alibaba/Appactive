---
layout: default
parent: 中文文档
nav_order: 4
---
# 开发指南(Naocs)

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

### 2.1 微服务（RPC）：Dubbo

**前置条件**

- 需要你的应用服务基于 Java 实现，并且以 Dubbo 实现服务调用

#### 入口应用

入口应用负责从流量中提取路由标，并设置到上下文中

**改造步骤**

1. 引入 maven 依赖

    ```
    <dependency>
        <groupId>com.alibaba.msha</groupId>
        <artifactId>client-bridge-servlet</artifactId>
        <version>0.3</version>
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
        <groupId>com.alibaba.msha</groupId>
        <artifactId>client-bridge-rpc-apache-dubbo2</artifactId>
        <version>0.3</version>
    </dependency>
    <dependency>
        <groupId>com.alibaba.msha</groupId>
        <artifactId>client-bridge-rpc-apache-dubbo2-metainfo</artifactId>
        <version>0.3</version>
    </dependency>
    <dependency>
        <groupId>com.alibaba.msha</groupId>
        <artifactId>client-spi-metainfo</artifactId>
        <version>0.3</version>
    </dependency>
    <dependency>
        <groupId>com.alibaba.msha</groupId>
        <artifactId>client-rule</artifactId>
        <version>0.3</version>
    </dependency>
    ```

2. 在 provider 中加入属性，如果是注解形式如下

    ```
    package io.appactive.demo.product.service;
    
    import io.appactive.demo.common.entity.Product;
    import io.appactive.demo.common.entity.ResultHolder;
    import io.appactive.demo.common.service.dubbo.ProductServiceUnit;
    import io.appactive.demo.product.repository.ProductRepository;
    import org.apache.dubbo.config.annotation.DubboService;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.beans.factory.annotation.Value;
    
    @DubboService(version = "1.0.0", group = "appactive", parameters = {"rsActive","unit","routeIndex","0"})
    public class ProductServiceUnitImpl implements ProductServiceUnit {
    
        @Value("${appactive.unit}")
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

核心是加上注解` parameters = {"rsActive","unit","routeIndex","0"} `
rsActive 为 unit 表明这是一个单元服务，routeIndex 为 0 表明路由ID是第 0 个参数。
rsActive 的 候选 value 有:

- normal: 普通服务，不做多活改造，按原有逻辑进行发现和调用的服务
- unit: 单元服务，基于规则，仅在本单元路由的服务
- center: 中心服务，强一致的业务（例如库存、金额等）的服务，强制路由到中心机房

对于单元服务，支持显式调用和隐式调用。其中显式调用需要如上改造方法签名，并用 routeIndex 注明路由ID参数位置。

而隐式调用则不需要改造方法签名，仅需要在实际调用方法前手工设置路由ID `AppContextClient.setUnitContext("12");`

最后，引入单元保护过滤器, 以springboot为例，在 application.properties 中加入一行:
`dubbo.provider.filter=unitProtectionFilter`

### 2.2 微服务（RPC）：SpringCloud

**前置条件**

- 需要你的应用服务基于 Java 实现，并且以 SpringCloud 实现服务调用
- 负载均衡支持 Ribbon，暂不支持 SpringCloudBalancer
- 支持声明式http客户端：Feign 和 RestTemplate，暂不支持 原始Http客户端如 OkHttp 和 HttpClient

#### 入口应用

同 Dubbo

#### 所有应用

**改造步骤**

1. 在 provider 和 consumer 都引入 maven 依赖

    ```
    <dependency>
        <groupId>com.alibaba.msha</groupId>
        <artifactId>client-bridge-rpc-springcloud-common</artifactId>
        <version>0.3</version>
    </dependency>
    ```
   
    对于 使用 Nacos 作为注册中心的应用，还应引入
    ```
    <dependency>
        <groupId>com.alibaba.msha</groupId>
        <artifactId>client-bridge-rpc-springcloud-nacos</artifactId>
        <version>0.3</version>
    </dependency>
    ```
    
    对于 使用 Eureka 作为注册中心的应用，还应引入
    ```
    <dependency>
       <groupId>com.alibaba.msha</groupId>
       <artifactId>client-bridge-rpc-springcloud-eureka</artifactId>
       <version>0.3</version>
    </dependency>
    ```
    
    注意，不同的注册中心不能同时使用。
    
    然后引入自动配置
    
    `@Import({ConsumerAutoConfig.class, NacosAutoConfig.class})`

2. 在 consumer 的 maven 中引入切面
    ```
    <build>
        <plugins>
            </plugin>
            <plugin>
                <groupId>org.codehaus.mojo</groupId>
                <artifactId>aspectj-maven-plugin</artifactId>
                <version>1.11</version>
                <configuration>
                    <aspectLibraries>
                        <aspectLibrary>
                            <groupId>com.alibaba.msha</groupId>
                            <artifactId>client-bridge-rpc-springcloud-common</artifactId>
                        </aspectLibrary>
                    </aspectLibraries>
                    <source>${maven.compiler.source}</source>
                    <target>${maven.compiler.target}</target>
                    <complianceLevel>1.8</complianceLevel>
                    <forceAjcCompile>true</forceAjcCompile>
                </configuration>
                <executions>
                    <execution>
                        <id>compileId</id>
                        <phase>compile</phase>
                        <goals>
                            <goal>compile</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
    ```
    植入多活寻址逻辑

3. 在 provider 中定义不同uri的属性，支持 ant 模式的 uri，举例

    ```
        @Bean
        public FilterRegistrationBean<UnitServiceFilter> appActiveUnitServiceFilter() {
            FilterRegistrationBean<UnitServiceFilter> filterRegistrationBean = new FilterRegistrationBean<>();
            UnitServiceFilter reqResFilter = new UnitServiceFilter();
            filterRegistrationBean.setFilter(reqResFilter);
            filterRegistrationBean.addUrlPatterns("/detailHidden/*","/detail/*");
            return filterRegistrationBean;
        }
    
        @Bean
        public FilterRegistrationBean<CenterServiceFilter> appActiveCenterServiceFilter() {
            FilterRegistrationBean<CenterServiceFilter> filterRegistrationBean = new FilterRegistrationBean<>();
            CenterServiceFilter reqResFilter = new CenterServiceFilter();
            filterRegistrationBean.setFilter(reqResFilter);
            filterRegistrationBean.addUrlPatterns("/buy/*");
            return filterRegistrationBean;
        }
    ```
    不同服务类型同 Dubbo，具体如下
 
    - center: 中心服务，强一致的业务（例如库存、金额等）的服务，强制路由到中心机房，使用 `CenterServiceFilter` 过滤
    - unit: 单元服务，基于规则，仅在本单元路由的服务，使用 `UnitServiceFilter` 过滤
    - normal: 普通服务，不做多活改造，使用 `NormalServiceFilter` 过滤，本类服务亦可不单独配置，除上述两种服务以外都认为是普通服务
    
### 3.1 数据库（DB）：Mysql

**前置条件**

- 需要你的数据库是 MySQL（5.x） ，且最终写库的应用基于 Java 实现

**改造步骤**

1. 引入 maven 依赖

    ```
    <dependency>
        <groupId>com.alibaba.msha</groupId>
        <artifactId>client-spi-metainfo</artifactId>
        <version>0.3</version>
    </dependency>
     <dependency>
        <groupId>com.alibaba.msha</groupId>
        <artifactId>client-bridge-db-mysql</artifactId>
        <version>0.3</version>
    </dependency>
    ```
   
2. 数据库连接加上参数，如
`jdbc:mysql://mysql:3306/product?characterEncoding=utf8&useSSL=false&serverTimezone=GMT&activeInstanceId=mysql&activeDbName=product`。其中：
	- activeInstanceId: 数据库实例ID
	- activeDbName: 数据库名字
	- activePort: 数据库实例端口，3306可以不填

3. 更换 driver，如: `spring.datasource.driver-class-name=io.appactive.db.mysql.driver.Driver`

### 4.1 基本配置

凡是依赖 `appactive-java-api` 模块的应用，启动时候都要配置参数：

```
-Dappactive.channelTypeEnum=NACOS
-Dappactive.namespaceId=appactiveDemoNamespaceId
```

表征当前应用使用 Nacos 作为命令通道，并且使用 appactiveDemoNamespaceId空间。
该空间需要有一些几个 dataId（下面管控面进行说明），这些 dataId 的 groudId 必须一致，比如默认为 `appactive.groupId`
当然这些都可以在启动参数进行配置，如

```
-Dappactive.dataId.idSourceRulePath=someDataId
-Dappactive.dataId.transformerRulePath=otherDataId
......
-Dappactive.groupId=myGroupId
```

## 二、管控面

在应用部署完成后要进行基线推送，在希望调整流量时进行切流。核心是规则的构造和推送，这里重点将几个规则进行说明。

- appactive.dataId.idSourceRulePath，举例：

    ```
    {
        "source": "arg,header,cookie",
        "tokenKey": "r_id"
    }
    ```

    说明，从http parameter、header、cookie 中按顺序寻找以r_id为key的value，找到一个即终止寻找过程。
    
- appactive.dataId.transformerRulePath，举例：

    ```
    {
      "id": "userIdBetween",
      "mod": "10000"
    }
    ```

    说明，提取到路由标后按照10000取模，作为最终路由标。


- appactive.dataId.trafficRouteRulePath，举例：

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

- appactive.dataId.forbiddenRulePath，举例：

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
