# AppActive Demo

## Overall structure
![appactive_landscape](https://appactive.oss-cn-beijing.aliyuncs.com/images/AppActive-demo.png)

The overall structure of this demo is shown in the figure.

Note: The registry nacos and database mysql that the application depends on are not shown in the figure.

There are 2 units:

-center: center unit
-unit: ordinary unit

There are 4 applications in total, according to the distance (call link) of the end user from near and far:

- gateway: Multi-active gateway, which distributes user traffic
- frontend: frontend application, accept user requests, and return after requesting actual data
- product: product application, providing three services:
    - product List: General Service
    - product Details: Unit Service
    - product order: central service, relying on inventory application
- storage: Inventory application, for ordering service to deduct inventory

For the sake of simplicity, the gateway is only deployed in the center, and the rest of the applications are deployed in each of the center and unit.

The green grid in the figure represents the call link of this request.

## Quick Start

### Premise
This demo requires the following software to be installed

- docker && docker-compose
- curl

note: this demo contains many applications，please adjust your memory settings to avoid failures; only x86 supported

### Step

1. Run `sh run.sh` in the `appactive-demo` module to start all applications
2. Bind hosts: `127.0.0.1 demo.appactive.io`, and then visit `http://demo.appactive.io/buyProduct?r_id=2000` to see how it works
3. Run `sh cut.sh` in the `appactive-portal` module to switch flow. It should be noted that the writing-forbidden rules of this demo are hard-coded. If you want to change the range, you need to calculate the writing-forbidden rules and the next-routing rules, and then execute the flow switch.

> If you plan to stop the experience, you can proceed: `cd appactive-demo` -> `docker-compose down`

## Build From Source

### Premise

- as mentioned above

### Step

1. Enter the nginx-plugin directory of the `appactive-gateway` module and mark it as a mirror: `docker build --build-arg UNITFLAG=center -t app-active/gateway:1.2-SNAPSHOT .`
2. Enter the `appactive-demo` module, maven build to get the jar package
3. Run `sh baseline.sh 2` in the `appactive-portal` module to push the application baseline
4. Run `sh run.sh` in the `appactive-demo` module to start all applications and gateway
5. Run `sh baseline.sh 3` in the `appactive-portal` module to push the gateway baseline
6. Bind the local host: `127.0.0.1 demo.appactive.io`, visit the browser `http://demo.appactive.io/buyProduct?r_id=2000` to see the effect
7. Run `sh cut.sh` in the `appactive-portal` module to cut the flow. The cut flow commands is:  `./cut.sh`. It should be noted that the write prohibition rules of this demo are hard-coded. If the user wants to change the cut flow range, he needs to calculate the write prohibition rule and the next routing rule by himself, and then execute the cut flow.

## Modules Experience 

### Filter

#### step

1. In `appactive-portal`, run `sh baseline.sh 2`
2. build all jar needed
3. run java application

```shell script
java -Dappactive.machineRulePath=/Path-to-Appactive/appactive-demo/data/frontend-unit/machine.json \
-Dappactive.dataScopeRuleDirectoryPath=/Path-to-Appactive/appactive-demo/data/frontend-unit \
-Dappactive.forbiddenRulePath=/Path-to-Appactive/appactive-demo/data/frontend-unit/forbiddenRule.json \
-Dappactive.trafficRulePath=/Path-to-Appactive/appactive-demo/data/frontend-unit/idUnitMapping.json \
-Dappactive.transformerRulePath=/Path-to-Appactive/appactive-demo/data/frontend-unit/idTransformer.json \
-Dappactive.idSourceRulePath=/Path-to-Appactive/appactive-demo/data/frontend-unit/idSource.json \
-Dio.appactive.demo.unit=unit \
-Dio.appactive.demo.app=frontend \
-Dio.appactive.demo.unitlist=center,unit \
-Dio.appactive.demo.applist=frontend,product,storage \
-Dserver.port=8886 \
-jar frontend-1.2-SNAPSHOT.jar
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

#### 步骤

1. in `appactive-portal` module, run `sh baseline.sh 2`
2. delete `DubboService` in `OrderServiceImpl.java`，and then build all jars
3. in `appactive-demo` module, run
```shell script
cd dependency/mysql && sh run.sh
```
then 
```shell script
# enter container
docker exec -ti appactive-mysql bash
# import data
mysql -uroot -pdemo_appactiive_pw product < /root/init.sql
# exit
exit 
```
4. run java

```shell script
java -Dappactive.machineRulePath=/Users/mageekchiu/Documents/workspace/Appactive/appactive-demo/data/frontend-unit/machine.json \
     -Dappactive.dataScopeRuleDirectoryPath=/Users/mageekchiu/Documents/workspace/Appactive/appactive-demo/data/storage-unit \
     -Dappactive.forbiddenRulePath=/Users/mageekchiu/Documents/workspace/Appactive/appactive-demo/data/storage-unit/forbiddenRule.json \
     -Dappactive.trafficRulePath=/Users/mageekchiu/Documents/workspace/Appactive/appactive-demo/data/storage-unit/idUnitMapping.json \
     -Dappactive.transformerRulePath=/Users/mageekchiu/Documents/workspace/Appactive/appactive-demo/data/storage-unit/idTransformer.json \
     -Dappactive.idSourceRulePath=/Users/mageekchiu/Documents/workspace/Appactive/appactive-demo/data/storage-unit/idSource.json \
     -Dio.appactive.demo.unit=unit \
     -Dio.appactive.demo.app=storage \
     -Dspring.datasource.url="jdbc:mysql://127.0.0.1:3306/product?characterEncoding=utf8&useSSL=false&serverTimezone=GMT&activeInstanceId=mysql&activeDbName=product" \
     -Dserver.port=8882 \
-jar storage-1.2-SNAPSHOT.jar
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

## Rule description

After running all applications, we ran baseline.sh and actually did the following things:

- Push rules to other applications through file channels
- Push rules to gateway through http channel

The rules include

- idSource.json: Describes how to extract routing labels from http traffic
- idTransformer.json: Describe how to parse the routing mark
- idUnitMapping.json: Describe the mapping relationship between the routing mark and the unit
- machine.json: describes the attribution unit of the current machine
- mysql-product: describe the attributes of the database

### Cut flow
Mainly do the following things when cutting flow:

- Build new mapping rules and banning rules (manually)
- Push new mapping rules to gateway
- Push banning rules to other apps
- Wait for the data to tie and push the new mapping relationship rules to other applications

Note that the new mapping relationship is the target state you want to achieve, and the prohibition rule is the difference calculated based on the target state and the status quo. Currently, both of these need to be manually set and updated to the corresponding json file under `appactive-portal/rule`, and then run `./cut.sh `
