# AppActive Architecture

---

## basic introduction
![active_architecture_global](https://appactive.oss-cn-beijing.aliyuncs.com/images/architecture.jpg)

The core business model of AppActive is divided into four parts, as shown in the figure above:

- Configuration: Management and control surface-disaster recovery configuration. Based on the Configuration module, the application can quickly define and manage disaster tolerance resources, configure basic disaster tolerance strategies, and complete daily initial access and operation and maintenance functions.
- Execute: Management and control-disaster recovery execution. Based on the Execute module, core actions are executed in drills or disaster scenarios to ensure business continuity and promote rapid business recovery.
- ApplicationTraffic: Data plane-application traffic. Based on the configuration of the Configuration module, unified management of the north-south and east-west traffic of the application is carried out in the Traffic module. In daily scenarios, to ensure the stability and experience of business traffic, the traffic is enclosed in the cell. Disaster scenarios, based on the core actions of the Execute module, perform traffic minute-level scheduling.
- Data: Data plane-data. Based on the Data module, the business can quickly perform data redundancy backup to ensure data recovery in disaster scenarios.

The current open source content of this project mainly covers the core standards of the ApplicationTraffic module, and a small amount is in the Configuration and Execute modules, and the Data module is scheduled to be open sourced.

## ApplicationTraffic
![traffic_model](https://appactive.oss-cn-beijing.aliyuncs.com/images/traffic_model.jpg)

In the ApplicationTraffic module, we build the module around three parts:

- Bridge: Business component bridge. When an application completes a business request, multiple middleware components are involved, such as entry gateways, microservices, messages, databases, etc. The core function of bridge is to adapt these components to achieve the purpose of multi-active flow control, which depends on the downstream Rule module.
- Rule: More live rules. When the application performs multi-active disaster recovery, it involves IDC definition, machine node definition, flow rule definition, gray-scale rule definition, a series of multi-active rule descriptions, and the smooth launch and daily use of the application.
- Channel: Data channel. Multi-active rules will be dynamically adjusted in daily and disaster scenarios, involving data interaction between different channels.


![traffic_target_mw](https://appactive.oss-cn-beijing.aliyuncs.com/images/traffic_bridge_mw.jpg)

Regarding the type of traffic, we based on the traffic request link, from top to bottom, the traffic can be divided into the layers as shown in the figure above:

- Access layer: Ingress traffic request, general business application entrance will have a layer of gateway. AppActive provides standard flow control plug-ins at this layer to smoothly support application ingress flow control.
- Service layer: The synchronous call method of internal applications, generally restful direct call or standard microservice framework. AppActive provides standard microservice control plug-ins at this layer to adapt to different microservice invocation methods.
- Message layer: The asynchronous calling method of internal applications, generally general MQ, such as RocketMQ, Kafka, etc. AppActive provides standard MQ control plug-ins at this layer to adapt to different message forms.
- Data layer: The application accesses the database. The database generally includes mysql, nosql, and Oracle. AppActive provides standard database control plug-ins at this layer to ensure data quality and adapt to different database forms.