---
layout: default
parent: 中文文档
---
# AppActive 架构

---

## 基本介绍
![active_architecture_global](https://appactive.oss-cn-beijing.aliyuncs.com/images/architecture.jpg)

AppActive 在业务模型上核心分为四部分，如上图所示：

- Configuration：管控面-容灾配置。基于 Configuration 模块，应用可快速进行容灾资源定义管理，基础容灾策略配置，完成日常初始化接入和运维功能。
- Execute：管控面-容灾执行。基于 Execute 模块，在演练或灾难场景下，核心动作执行，保障业务连续性，促使业务快速恢复。
- ApplicationTraffic：数据面-应用流量。基于 Configuration 模块配置，在 Traffic 模块中对应用的南北向和东西向流量进行统一纳管。日常场景下，保障业务流量稳定性和体验，流量封闭在单元内。灾难场景，基于 Execute 模块的核心动作，进行流量分钟级调度。
- Data：数据面-数据。基于 Data 模块，业务可快速进行数据冗余备份，保障灾难场景下的数据恢复。

本次项目当前开源的内容，主要覆盖在 ApplicationTraffic 模块核心标准，少量在 Configuration 和 Execute 模块，Data 模块排期开源中。

## ApplicationTraffic
![traffic_model](https://appactive.oss-cn-beijing.aliyuncs.com/images/traffic_model.jpg)

在 ApplicationTraffic 模块，我们围绕三部分进行模块建设：

- Bridge： 业务组件桥。应用完成一次业务请求，会涉及多个中间件组件，例如入口网关、微服务、消息、数据库等。bridge 核心功能即做这些组件的适配，达到多活流量控制的目的，其依赖下游 Rule 模块。
- Rule： 多活规则。应用进行多活容灾时，涉及IDC定义、机器节点定义、流量规则定义、灰度规则定义，一系列多活规则描述及保障应用多活的平稳上线及日常使用。
- Channel：数据通道。多活规则在日常和灾难场景下会动态调整，涉及不同通道的数据交互。


![traffic_target_mw](https://appactive.oss-cn-beijing.aliyuncs.com/images/traffic_bridge_mw.jpg)

针对流量类型而言，我们基于流量请求链路，自顶而下，可以把流量分为如上图的层：

- 接入层： 入口流量请求，一般业务应用入口会有一层网关。AppActive 在这一层提供标准的流量控制插件，平滑支持应用的入口流量控制。
- 服务层： 内部应用的同步调用方式，一般为 restful 直连调用或标准微服务框架。AppActive 在这一层提供标准的微服务控制插件，适配不同微服务调用方式。
- 消息层： 内部应用的异步调用方式，一般为通用的 MQ，例如 RocketMQ、Kafka等。AppActive 在这一层提供标准的MQ控制插件，适配不同的消息形态。
- 数据层:  应用访问数据库，数据库一般有 mysql、nosql、Oracle。AppActive 在这一层提供标准的数据库控制插件，保障数据质量，适配不同的数据库形态。


