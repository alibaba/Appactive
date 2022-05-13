---
layout: default
parent: 中文文档
---
# AppActive 样例(文件)

## 整体架构

![appactive_landscape](https://appactive.oss-cn-beijing.aliyuncs.com/images/AppActive-demo.png)

本 demo 整体架构如图。 

注：应用共同依赖的注册中心 nacos 和 数据库 mysql 未在图中展示出来。

共有 2 个单元：

- center: 中心单元 
- unit: 普通单元

共有 4 个应用，按照距离（调用链路）终端用户由近及远分别为：

- gateway: 多活网关，分发用户流量
- frontend: 前端应用，接受用户请求，请求到实际数据后返回
- product: 产品应用，提供三个服务：
	- 产品列表: 普通服务
	- 产品详情: 单元服务
	- 产品下单: 中心服务，依赖库存应用
- storage: 库存应用，供下单服务扣减库存

为简化起见，gateway 仅部署在 center，其余应用在 center 和 unit 各部署一套。

图中绿色格子代表了本次请求的调用链路。

## 快速启动

### 前提

要求安装如下软件

- docker && docker-compose
- curl

注：本 demo 包含较多应用，请调大 docker 内存，避免闪退；仅支持x86

### 步骤

1. 在 `appactive-demo` 模块中运行 `sh run-quick.sh` ，启动所有应用
2. 绑定本地 host: `127.0.0.1 demo.appactive.io`，浏览器访问 `http://demo.appactive.io/buyProduct?r_id=2000` 查看效果
3. 在`appactive-portal` 模块中运行 `sh cut.sh` 进行切流 。需要注意的是，本 demo 的禁写规则是写死的，用户若要更换切流范围则需自行计算禁写规则和下次路由规则，然后执行切流。

> 如果你打算停止体验，可进行：`cd appactive-demo` -> `docker-compose down`

## 源码构建

### 前提

- 如上

### 步骤

1. 进入 `appactive-gateway/nginx-plugin` 目录，将其打成镜像：`docker build --build-arg UNITFLAG=center -t app-active/gateway:0.3 .`
2. 进入 `appactive-demo` 模块，maven build 获得 jar 包
3. 在 `appactive-portal` 模块中运行 `sh baseline.sh 2`，推送应用基线
4. 在 `appactive-demo` 模块中运行 `sh run.sh` ，启动所有应用和网关
5. 在 `appactive-portal` 模块中运行 `sh baseline.sh 3`，推送网关基线
6. 绑定本地 host: `127.0.0.1 demo.appactive.io`，浏览器访问 `http://demo.appactive.io/buyProduct?r_id=2000` 查看效果
7. 在 `appactive-portal` 模块中运行 `sh cut.sh` 进行切流。需要注意的是，本 demo 的禁写规则是写死的，用户若要更换切流范围则需自行计算禁写规则和下次路由规则，然后执行切流。

## 分模块体验

### Filter

#### 步骤

1. 在 `appactive-portal` 模块中运行 `sh baseline.sh 2`，推送应用基线
2. 构建相关 jar 包
3. 运行

```
java -Dappactive.machineRulePath=/Path-to-Appactive/appactive-demo/data/frontend-unit/machine.json \
-Dappactive.dataScopeRuleDirectoryPath=/Path-to-Appactive/appactive-demo/data/frontend-unit \
-Dappactive.forbiddenRulePath=/Path-to-Appactive/appactive-demo/data/frontend-unit/forbiddenRule.json \
-Dappactive.trafficRulePath=/Path-to-Appactive/appactive-demo/data/frontend-unit/idUnitMapping.json \
-Dappactive.transformerRulePath=/Path-to-Appactive/appactive-demo/data/frontend-unit/idTransformer.json \
-Dappactive.idSourceRulePath=/Path-to-Appactive/appactive-demo/data/frontend-unit/idSource.json \
-Dappactive.unit=unit \
-Dappactive.app=frontend \
-Dio.appactive.demo.unitlist=center,unit \
-Dio.appactive.demo.applist=frontend,product,storage \
-Dserver.port=8886 \
-jar frontend-0.3.jar
```

4. 测试

```
curl 127.0.0.1:8886/show?r_id=1 -H "r_id:2" -b "r_id=3"
routerId: 1
curl 127.0.0.1:8886/show -H "r_id:2" -b "r_id=3"
routerId: 2
curl 127.0.0.1:8886/show  -b "r_id=3"
routerId: 3
curl 127.0.0.1:8886/show  
routerId: null
```

### MySQL

#### 步骤

1. 在 `appactive-portal` 模块中运行 `sh baseline.sh 2`，推送应用基线
2. 在 `appactive-demo`中 运行 nacos

```
cd dependency/nacos && sh run.sh
```

3. 在 `appactive-demo`中 运行 mysql

```
cd dependency/mysql && sh run.sh
```

然后 运行

```
# 进入容器
docker exec -ti appactive-mysql bash
# 导入数据
mysql -uroot -pdemo_appactiive_pw product < /root/init.sql
# 退出
exit 
```

4. 构建所有 jar 包并运行

```
java -Dappactive.machineRulePath=/Path-to-Appactive/appactive-demo/data/storage-unit/machine.json \
     -Dappactive.dataScopeRuleDirectoryPath=/Path-to-Appactive/appactive-demo/data/storage-unit \
     -Dappactive.forbiddenRulePath=/Path-to-Appactive/appactive-demo/data/storage-unit/forbiddenRule.json \
     -Dappactive.trafficRulePath=/Path-to-Appactive/appactive-demo/data/storage-unit/idUnitMapping.json \
     -Dappactive.transformerRulePath=/Path-to-Appactive/appactive-demo/data/storage-unit/idTransformer.json \
     -Dappactive.idSourceRulePath=/Path-to-Appactive/appactive-demo/data/storage-unit/idSource.json \
     -Dappactive.unit=unit \
     -Dappactive.app=storage \
     -Dspring.datasource.url="jdbc:mysql://127.0.0.1:3306/product?characterEncoding=utf8&useSSL=false&serverTimezone=GMT&activeInstanceId=mysql&activeDbName=product" \
     -Dserver.port=8882 \
-jar storage-0.3.jar
```

5. 测试

```
curl 127.0.0.1:8882/buy?r_id=1 
routerId 1 bought 1 of item 12, result: success
curl 127.0.0.1:8882/buy?r_id=4567 
routerId 4567 bought 1 of item 12, result: machine:unit,traffic:CENTER,not equals 

```

### Gateway

请见 [nginx-plugin](/appactive-gateway/nginx-plugin/Readme.md)

### Dubbo

构建 Dubbo 的 demo 过于复杂，建议使用 quick start 中启用的demo，直接进行体验，特别地，单元保护功能测试步骤如下：
1. 首先修改 frontend 的规则，以便于 frontend 访问到错误的单元

```
cd data/frontend-center
vim idUnitMapping.json
```

如下

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

2. 发起测试

```

curl 127.0.0.1:8885/detail -H "Host:demo.appactive.io" -H "r_id:2499" 
# 注意到报错会有这样一段
[appactive/io.appactive.demo.common.service.dubbo.ProductServiceUnit:1.0.0] [detail] from [172.18.0.9] is rejected by UnitRule Protection, targetUnit [CENTER], currentUnit [unit].)
```

因为我们修改了规则，让 frontend-center 将 路由id为 2499 的 请求路由到了单元，但实际上，这个请求应该路由到中心，所以被单元的provider拒绝请求了。

## 规则说明

### 基线

在运行所有应用后，我们运行了 baseline.sh，实际上做了如下几件事：

- 通过 文件 通道给 其他应用 推送规则
- 通过 http 通道给 gateway 推送规则

规则包括

- idSource.json: 描述如何从 http 流量中提取路由标
- idTransformer.json: 描述如何解析路由标
- idUnitMapping.json: 描述路由标和单元的映射关系
- machine.json: 描述当前机器的归属单元
- mysql-product: 描述数据库的属性

### 切流

切流时主要做了如下几件事：

- 构建新的映射关系规则和禁写规则（手动）
- 将新的映射关系规则推送给gateway
- 将禁写规则推送给其他应用
- 等待数据追平后将新的映射关系规则推送给其他应用

注意，新的映射关系是你想达到的目标状态，而禁写规则是根据目标状态和现状计算出来的差值。当前，这两者都需要你手动设置并更新到 `appactive-portal/rule` 下对应的json文件中去，然后运行 `./cut.sh`


