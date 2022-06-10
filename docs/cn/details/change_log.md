---
layout: default
parent: 中文文档
nav_order: 9
---
# 更新记录

---

## 0.1
<span style="color:#999CA2;">2021.12.20</span>
第一个开源版本发布：

1. 多活核心规则模型。定义机房、流量等多个核心组件
2. 网关插件发布，支持流量解析、机器打标、路由规则、路由透传等能力
3. 微服务插件发布，支持路由透传，多活服务属性策略、纠错及保护能力
4. 消息层组件API定义发布
5. 数据层组件发布，支持数据保护
6. 基础 Demo 和 Portal

## 0.2
1. 支持自定义路由标提取规则
2. RPC(Dubbo)支持单元保护
3. 新增轻量化的 demo 和 quick start
4. jar 包发布到公共maven仓库

## 0.3
1. rule 和 channel 解耦，新增nacos channel支持
2. RPC 新增 SpringCloud 支持
3. 新增 [官方文档](https://doc.appactive.io/docs/cn/README_CN.html) 和 [官方demo站点](http://demo.appactive.io/)