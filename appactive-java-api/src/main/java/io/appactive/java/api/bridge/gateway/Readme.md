---
layout: default
parent: CNReadme
---
# AppActive GateWay 设计标准
网关作为一个复杂系统面对用户的最前线，具有路由转发、负载均衡、灰度、限流、容灾、信息安全等重要的作用。
本模块作为多活容灾系统的网关，重点解决的是其中多活路由的问题，简单定义就是 **"确定哪些流量应该被路由到哪些 server"** 。

从定义可以分析出，**多活网关** 应该具备三大功能：

 - **流量识别**：从流量中提取标识，确定流量的某个/些特征（比如属于哪个用户，或者哪个地区），这类特征称为**路由标**
 - **server 管理**：维护一个 server 列表，并给 server 打上特定标签（比如属于哪个地区），这类标签在异地多活场景下称为**单元标**
 - **流量与 server 的映射规则**：将具备特定特征的流量路由到具备特定标签的 server 进行处理

三大功能的伪代码如下：

```
# 1
routerId = extractFrom(request)

# 2
serverMap = {
    center : [ip1, ip2, ip3]
    unit1  : [ip4, ip5, ip6]
}

# 3
rule = {
    center : {
        routerIdIn:[1, 2, 3, 4]
        routerIdInBetween:[1, 9000]
    } 
    unit1 : {
        routerIdIn:[8, 9, 10, 11]
        routerIdInBetween:[9001, 9999]
    }   
}
flag = getFlag(rule, routerId)
ip = balance(flag, serverMap)
route(request, ip)    
```

举个实际的应用场景：

 - 流量划分：从 http 请求中提取用户的省份 ID
 - server 划分：给所有的 server 打上其所在机房的省份标签
 - 流量与 server 的映射规则：将流量就近路由到某个 server
 
我们将具备上述三大功能的网关称为**多活网关**。规则如何定义、如何维护，没有强制要求，不同的实现可以有不同的方案。
本文在给出标准的同时，给出一个具体的实现，希望让读者有所收获。
请参考 gateway 模块
