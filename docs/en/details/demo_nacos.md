# AppActive Demo

## Overall architecture

![appactive_landscape](https://appactive.oss-cn-beijing.aliyuncs.com/images/demo.png)

The overall structure of this demo is shown in the figure.

Note: 
- The registry nacos and database mysql that the application depends on are not shown in the figure.
- The demo uses nacos as a command channel, the one with file can be seen [here](demo.md) 


There are 2 units:

- center: center unit
- unit: ordinary unit

There are 4 applications in total, according to the distance (call link) of the end user from near and far:

- gateway: Multi-active gateway, which distributes user traffic
- frontend: frontend application, accept user requests, and return after requesting actual data
- product: product application, providing three services:
    - product List: General Service
    - product Details: Unit Service
    - product order: central service, relying on inventory application
- storage: storage application, for ordering service to deduct inventory

For the sake of simplicity, the gateway is only deployed in the center, and the rest of the applications are deployed in each of the center and unit.

The green grid in the figure represents the call link of this request.

## Quick Start

### Premise
This demo requires the following software to be installed

- docker && docker-compose
- curl

note: this demo contains many applications，please adjust your memory settings to avoid failures; only x86 supported

### Step

1. Run `sh run-nacos-quick.sh` in the `appactive-demo` module to start all applications
2. Bind hosts: `127.0.0.1 demo.appactive.io`, and then visit `http://demo.appactive.io/buyProduct?r_id=2000` to see how it works
3. Run `sh cut.sh NACOS appactiveDemoNamespaceId` in the `appactive-portal` module to switch flow. It should be noted that the writing-forbidden rules of this demo are hard-coded. If you want to change the range, you need to calculate the writing-forbidden rules and the next-routing rules, and then execute the flow switch.

> If you plan to stop the experience, you can proceed: `cd appactive-demo` -> `sh quit.sh`

## Build From Source

### Premise

- as mentioned above

### Step

1. Enter `appactive-gateway/nginx-plugin` dir and build image: `docker build --build-arg UNITFLAG=center -t app-active/gateway:0.3 .`
2. In the root directory,  run maven-build command to get all the jars
3. Run `sh run-nacos.sh 1` in the `appactive-demo `module ，then visit `127.0.0.1:8848/nacos` to create a namespace for command channel，like `appactiveDemoNamespaceId`
4. Run `sh baseline.sh 2 NACOS appactiveDemoNamespaceId` in the `appactive-portal` module to push the application baseline
5. Run `sh run-nacos.sh 2 appactiveDemoNamespaceId` in the `appactive-demo` module to start all applications and gateway
6. Run `sh baseline.sh 3` in the `appactive-portal` module to push the gateway baseline
7. Bind the local host: `127.0.0.1 demo.appactive.io`, visit the browser `http://demo.appactive.io/buyProduct?r_id=2000` to see the effect
8. Run `sh cut.sh NACOS appactiveDemoNamespaceId` in the `appactive-portal` module to cut the flow. It should be noted that the write-prohibition rules of this demo are hard-coded. If the user wants to change the cut flow range, he needs to calculate the write-prohibition rules and the next-routing-rule by himself, and then execute the cutting command.

## Modules Experience 

### Premise

1. Run nacos in `appactive-demo`
```shell script
cd dependency/nacos && sh run.sh

# and then create a namespace for command channel，like `appactiveDemoNamespaceId`
```

2. Run maysql in `appactive-demo`
```shell script
cd dependency/mysql && sh run.sh
```

### Filter

#### steps

1. In `appactive-portal`, run `sh baseline.sh 2`
2. build all jar needed
3. run java application

```shell script
java -Dappactive.unit=unit \
-Dappactive.app=frontend \
-Dio.appactive.demo.unitlist=center,unit \
-Dio.appactive.demo.applist=frontend,product,storage \
-Dserver.port=8886 \
-Dappactive.channelTypeEnum=NACOS \
-Dappactive.namespaceId=appactiveDemoNamespaceId \
-jar frontend-0.3.jar
```
4. test
```shell script
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

#### steps

1. In `appactive-portal` module, run `sh baseline.sh 2 NACOS appactiveDemoNamespaceId`

2. Data initiation
```shell script
# enter container
docker exec -ti appactive-mysql bash
# import data
mysql -uroot -pdemo_appactiive_pw product < /root/init.sql
# exit
exit 
```
4. build all the jars and run

```shell script
java -Dappactive.channelTypeEnum=NACOS \
     -Dappactive.namespaceId=appactiveDemoNamespaceId \
     -Dappactive.unit=unit \
     -Dappactive.app=storage \
     -Dspring.datasource.url="jdbc:mysql://127.0.0.1:3306/product?characterEncoding=utf8&useSSL=false&serverTimezone=GMT&activeInstanceId=mysql&activeDbName=product" \
     -Dserver.port=8882 \
-jar storage-0.3.jar
```
5. test
```shell script
curl 127.0.0.1:8882/buy?r_id=1 
routerId 1 bought 1 of item 12, result: success
curl 127.0.0.1:8882/buy?r_id=4567 
routerId 4567 bought 1 of item 12, result: machine:unit,traffic:CENTER,not equals 

```

### Gateway

Visit [nginx-plugin](/appactive-gateway/nginx-plugin/Readme.md)

### Dubbo

the building process of demo of Dubbo is far too complicated，we suggest using demo in  "quick start": 
1. run test
```shell script
curl 127.0.0.1:8885/detail -H "Host:demo.appactive.io" -H "r_id:2499" 
 # you can see error logs as follows  
[appactive/io.appactive.demo.common.service.dubbo.ProductServiceUnit:1.0.0] [detail] from [172.18.0.9] is rejected by unit protection, targetUnit [CENTER], currentUnit [unit].)

```

because we modified rules, so that frontend-center would route request of routerId 2499 to unit. 
however，request like this should be routed to center, so provider in unit would deny such request.

## Rule description

After running all applications, we ran baseline.sh and actually did the following things:

- Push rules to other applications through nacos channel
- Push rules to gateway through http channel

The rules include

- appactive.dataId.idSourceRulePath: Describes how to extract routing labels from http traffic
- appactive.dataId.transformerRulePath: Describe how to parse the routing mark
- appactive.dataId.trafficRouteRulePath: Describe the mapping relationship between the routing mark and the unit
- appactive.dataId.dataScopeRuleDirectoryPath: describes the attribution unit of the current machine

### Cut flow
Mainly do the following things when cutting flow:

- Build new mapping rules and banning rules (manually)
- Push new mapping rules to gateway
- Push banning rules to other apps
- Wait for the data to tie and push the new mapping relationship rules to other applications

Note that the new mapping relationship is the target state you want to achieve, and the prohibition rule is the difference calculated based on the target state and the status quo. Currently, both of these need to be manually set and updated to the corresponding json file under `appactive-portal/rule`, and then run `./cut.sh `