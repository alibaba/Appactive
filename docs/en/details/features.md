> This article briefly introduces the features of AppActive

# Features

---

## One, unit diversion
### Unified traffic access
The ability to control traffic comes from the unified access of traffic. We call this layer the access layer. The access layer is the entrance of all traffic of the unit. It can parse the request protocol and correctly guide the request traffic to the corresponding application service SLB of the correct unit according to the set split mode and split granularity. If there are no special instructions below, the request protocol defaults to HTTP. The figure below is a schematic diagram of the access layer.

### Shunt mode
In the multi-active scenario, the traffic will be sliced ​​according to a certain latitude and distributed to different units. Multi-active supports multiple diversion modes, such as proportional diversion and diversion according to business attributes. The specific diversion mode to choose depends on the needs of the business scenario. The shunt mode is finally reflected in the routing rules. The routing rule is a configuration that defines which unit a certain flow should belong to.
1. Diversion proportionally<br/>
   In some business scenarios, you need to split traffic according to users or device numbers. Users, devices, etc. can all be abstracted into one ID, and then the ID can be divided into multiple sections, and each section belongs to a certain unit. In this shunt mode, the traffic ratio of each unit can be estimated through the ID interval. By setting the ratio of the ID interval to the unit capacity to be consistent, global load balancing can be achieved.
2. Diversion according to business attributes<br/>
   In some business scenarios, it is necessary to split traffic according to traffic attributes, such as introducing crawler traffic to a specific unit, or splitting traffic by province. Crawlers, provinces, etc. can all be abstracted into one label, and then different label value traffic can be introduced into different units. This offloading mode can support physical isolation of traffic with different business attributes.

### Shunt granularity
With the servicing of applications, today's applications are no longer a monolithic architecture, and there may be hundreds of applications that provide user access. The access layer introduces user traffic to the correct application, and supports two granularities of domain name and URI prefix.
1. Diversion by domain name<br/>
   Different applications are distinguished according to different domain names. For example, the domain name of application app1 is app1.example.com, and the domain name of app2 is app2.example.com.
2. Diversion by URI prefix<br/>
   (Not open source yet) Different applications are distinguished according to different URI prefixes. For example, the domain names of apps app1 and app2 are both app.example.com, but the traffic with the prefix /app1 in the URI is introduced into app1, and the traffic with the prefix /app2 is introduced into app2.

### Traffic Protocol
The access layer supports four-layer and seven-layer rich traffic request protocols to meet the needs of users in diverse scenarios such as the Internet and the Internet of Things.
1. HTTP protocol support<br/>
   The access layer supports the HTTP protocol by default, and the domain name and URI are parsed from the HTTP protocol for forwarding and routing.
2. HTTPS protocol support<br/>
   The access layer supports the HTTPS protocol, provides centralized domain name certificate management, and meets the user's requirements for reliable and secure transmission. If the user configures the domain name certificate at the access layer, the application of SLB does not need to configure the certificate.
3. Other protocol support<br/>
   In addition to supporting HTTP and HTTPS protocols, the access layer also supports other HTTP-based protocols, such as SOAP. The access layer has great scalability in terms of protocols, and can quickly support special protocols in the form of plug-ins, such as MQTT and COAP in the Internet of Things scenario.

### Route transparent transmission
In order to ensure that the flow can be closed in the cell, each layer of the access layer, application layer, and data layer will perform routing error correction or cell protection. In order to identify the traffic, it is necessary to clarify the unitized type and branch id of the traffic, which we call routing parameters, so that the correct unit of the traffic can be calculated through routing rules. Therefore, the routing parameters need to be transmitted along the request path, which we call routing Penetrate.

1. Access layer routing transparent transmission<br/>
   When a browser initiates a service request, it needs to carry routing parameters in the request. Routing parameters can be in cookie, head or body, cookie is recommended. The access layer can parse the HTTP request, get the routing parameters and route to the correct application SLB, while the application server can still get the native routing parameters from the request.
2. Application layer routing transparent transmission<br/>
   When the traffic arrives at the application server, MultiLive provides a plug-in to extract routing parameters from the HTTP request and save them in the context. The next application may initiate RPC calls or asynchronous messages. Therefore, routing parameters need to be transparently transmitted at the RPC and message levels.
3. RPC routing transparent transmission<br/>
   When an application initiates an RPC call, the RPC client can retrieve routing parameters from the context and follow the RPC request to the remote service provider Provider. The Provider client recognizes the routing parameters in the Request and saves them in the calling context. The process of routing parameters in RPC is transparent to users.
4. Message routing transparent transmission<br/>
   When the MQ client sends a message, it will obtain routing parameters from the current context and add it to the message properties. When the MQ client consumes a message, it can retrieve routing parameters from the message attributes and save them in the calling context. The process of routing parameters in MQ is transparent to users.
5. Data layer routing transparent transmission<br/>
   Dirty writing of data can cause serious consequences, so it is necessary to ensure that the data is stored in the correct unit of DB. Duohuo provides the DRIVER plug-in, and rejects requests from non-this unit.

## Two, unit collaboration
In the disaster recovery scenario, each unit in the ideal scenario is independent, but in fact there will be some business scenarios that cross-unit scenarios. In order to meet these scenarios, the product needs to provide unit collaboration capabilities.
### Center call
In some specific business scenarios, in order to ensure strong data consistency, specific services can only be provided in specific central units, and all calls to central services will be directly routed to the central unit for completion. Remote multi-active products use CSB components and RPC multi-active plug-ins to complete coordinated calls between service units to meet business integrity.

## Three, unit protection
The product guarantees the global correctness of the business logic, and will not cause inconsistencies in the unit business logic due to cut-flow operations. Each layer of the system from top to bottom has error correction protection capabilities for error traffic to ensure that services are flowed correctly according to the unitized rules.

### Access layer error correction
The traffic enters the access layer, and the access layer determines the unit of the traffic by requesting additional routing parameters, and the non-unit traffic will be proxied to the correct target unit, ensuring the correctness of the access layer ingress traffic.

### RPC error correction
When the RPC service is called, the RPC Multi-Live Plugin will perform the correct routing of the service call based on the requested unit information on the Consumer side, and call the wrong traffic service. The RPC Multi-Live Plugin will calculate the correct target unit and call across units. Target unit services to ensure the consistency of service circulation logic. At the same time, the RPC Multi-Live Plugin will perform a second check on the incoming request on the Provider side to ensure the correct service call. Through the double check mechanism, RPC Multi-Live Plugin realizes the error correction of RPC calls to ensure the correctness of service calls.

## Fourth, unit expansion
###Horizontal expansion
When the business carrying capacity of the existing unit has reached the upper limit and cannot be expanded, the product provides simple and fast unit horizontal expansion capabilities:
1. Expansion of new units nationwide without geographic restrictions
2. The number of new units to be expanded is not limited, and the stability and performance of the unit are not affected by the number of units
3. Two types of unit expansion are provided: remote unit with independent DB and same-city unit with shared DB