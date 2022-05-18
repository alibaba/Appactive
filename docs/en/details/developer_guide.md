---
layout: default
nav_order: 8
---
# Develop Guide(File)

---

## 1. Data Plane
### 1.1 Gateway: Nginx

**precondition**

-You need your gateway to be implemented based on nginx and have the ability to run lua

**Transformation steps**

Use the directory file under `appactive-gateway` as your gateway configuration file.

In particular, you need to render each domain name file of nginx according to the following template:

```
server {
    listen 80;

    server_name %VAR_DOMAIN% %VAR_CENTER_DOMAIN% %VAR_UNIT_DOMAIN%;

    include srv.cfg;

    location %VAR_URI% {
        set $app %VAR_APP_ID%;
        set $unit_type test1122;
        set $rule_id 459236fc-ed71-4bc4-b46c-69fc60d31f18;
        set $router_rule ${rule_id}_${unit_type};
        set $unit_key'';
        set $cell_key'';
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

in:

- VAR_DOMAIN: The domain name used directly by the end user, mandatory. The other two unit subdomains are optional
- VAR_URI: specific URI
- VAR_APP_ID: The only front end composed of domain name + URI. Need to replace `.` with `_` and `/` with `@`
- VAR_UNIT_ENABLE: Whether the current URI is addressed in multi-active mode, if it is 1, it is yes, if it is 0, it is no (go directly to this unit)
- UNIT_FLAG_N: unit flag of each unit
- VAR_BACKEND_IP_LIST: the back-end application IP of the corresponding unit

### 1.2 Microservices (RPC): Dubbo
**precondition**

-You need to implement your application services based on Java and implement service calls with Dubbo

#### Entry application

The entry application is responsible for extracting the routing beacon from the traffic and setting it in the context

**Transformation steps**

1. Introduce maven dependency

    ```
    <dependency>
        <groupId>com.alibaba.msha</groupId>
        <artifactId>client-bridge-servlet</artifactId>
        <version>0.3</version>
    </dependency>
    ```

2. import filter，for example

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

3. When the request comes, you can call `AppContextClient.getRouteId();` in the application to get the route ID

#### All applications
**Transformation steps**

1. Introduce maven dependency in both provider and consumer

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

2. Add attributes to the provider, if it is an annotation form as follows

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
            System.out.println("detail: "+ rId +": "+ pId);
            return new ResultHolder<>(productRepository.findById(pId).orElse(new Product()));
        }
    }
    
    ```

The core is to add annotations
`parameters = {"rsActive","unit","routeIndex","0"}`
If rsActive is unit, it indicates that this is a unit service, and a routeIndex of 0 indicates that the route ID is the 0th parameter.
The candidate values ​​of rsActive are:

- normal: normal service, which requires no multi-active modification, and will route as it was
- unit: unit service, which will only route within right unit according to multi-active rules
- center: center service, which will only route within center idc

For unit services, explicit invocation and implicit invocation are supported. The explicit call needs to modify the method signature as above, and use routeIndex to indicate the location of the route ID parameter.

The implicit call does not need to modify the method signature, just manually set the route ID before actually calling the method `AppContextClient.setUnitContext("12");`

Last but no least, we import unit protection filter. Take springboot as an example, adding one line in application.properties will do the trick:
`dubbo.provider.filter=unitProtectionFilter`

### 1.3 Database (DB): Mysql

**precondition**

-You need your database to be MySQL (5.x), and the final application to write the library is based on Java

**Transformation steps**

1. Introduce maven dependency

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
   
2. Add parameters to the database connection, such as
   `jdbc:mysql://mysql:3306/product?characterEncoding=utf8&useSSL=false&serverTimezone=GMT&activeInstanceId=mysql&activeDbName=product`. in:
   - activeInstanceId: database instance ID
   - activeDbName: database name
   - activePort: database instance port, 3306 can be left blank

3. Replace the driver, such as: `spring.datasource.driver-class-name=io.appactive.db.mysql.driver.Driver`

### 1.4 Basic configuration
All applications that rely on the `appactive-java-api` module must configure the parameter `-Dappactive.path=/path/to/path-address` when starting.
The content of path-address is:

```
{
    "appactive.machineRulePath":"/app/data/machine.json",
    "appactive.dataScopeRuleDirectoryPath":"/app/data",
    "appactive.forbiddenRulePath":"/app/data/forbiddenRule.json",
    "appactive.trafficRulePath":"/app/data/idUnitMapping.json",
    "appactive.transformerRulePath":"/app/data/idTransformer.json",
    "appactive.idSourceRulePath":"/app/data/idSource.json",
}

```
in which

- appactive.forbiddenRulePath: Describe which route flags are forbidden to write
- appactive.transformerRulePath: Describe how to parse the routing mark
- appactive.trafficRulePath: Describes the mapping relationship between route markers and units
- appactive.machineRulePath: Describe the attribution unit of the current machine
- appactive.dataScopeRuleDirectoryPath: Store the property file of the database, one file per database, the file name is: activeInstanceId-activeDbName or activeInstanceId-activeDbName-activePort
- appactive.idSourceRulePath: describe how we extract routerId from http traffic 

## 2. Control Plane

After the application is deployed, the baseline is pushed, and the flow is switched when you want to adjust the traffic. The core is the construction and push of rules, here are a few rules to explain.

- appactive.transformerRulePath, for example:

```
{
  "id": "userIdBetween",
  "mod": "10000"
}
```
