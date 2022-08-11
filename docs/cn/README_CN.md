---
layout: default
title: 中文文档
has_children: true
---
# 中文文档

> AppActive 是阿里巴巴开源的一款标准、通用且功能强大的，致力于构建应用多活架构的开源中间件

> 稳定压倒一切，基于AppActive驱动企业新高可用时代

[![License](https://img.shields.io/badge/license-Apache%202-4EB1BA.svg)](https://www.apache.org/licenses/LICENSE-2.0.html)
[![](https://img.shields.io/badge/Maven%20Central-v0.2-blue.svg)](https://mvnrepository.com/search?q=appactive)

<div style="text-align: center">
   <img src="https://appactive.oss-cn-beijing.aliyuncs.com/images/appactive-logo.jpg?x-oss-process=style/h400" />
</div>


---
## 基本介绍
AppActive，是一个面向业务应用构建云原生高可用多活容灾架构的开源中间件。它提供了应用多活容灾架构的标准、实现和 Demo，适用于丰富的业务场景（单 AZ、单 Region、单云、多 AZ、多 Region、多云、自建 IDC等）。

AppActive 建立在 阿里巴巴 使用 AHAS-MSHA 系统大规模运行生产应用系统的8年经验之上，且结合了来自阿里云商业化服务的外部多家客户和社区的最佳实践，具备高可靠、可拓展等特性。

## 概念&设计
![appactive_landscape](https://appactive.oss-cn-beijing.aliyuncs.com/images/aactive_landscape.jpg?x-oss-process=style/h600)

AppActive 整体架构覆盖数据面和管控面，全流程管理应用流量和数据，如上图，详细内容可见下述的链接：

- [架构(Architecture)](details/architecture.md)：介绍 Appactive 部署架构和技术架构。
- [概念(Concept)](details/concept.md)：介绍 Appactive 的基本概念模型。
- [特性(Features)](details/features.md)：介绍 Appactive 实现的功能特性。


## 快速体验
![appactive_landscape](https://appactive.oss-cn-beijing.aliyuncs.com/images/demo.png?x-oss-process=style/h200)

### 前提
本 demo 要求安装如下软件
- docker && docker-compose
- curl

注：本 demo 包含较多应用，请调大 docker 内存，避免闪退。

### 步骤

1. 在 `appactive-demo` 模块中运行 `sh run-nacos-quick.sh` ，启动所有应用
2. 绑定本地 host: `127.0.0.1 demo.appactive.io`，浏览器访问 `http://demo.appactive.io/buyProduct?r_id=2000` 查看效果
3. 在`appactive-portal` 模块中运行 `sh cut.sh` 进行切流 。需要注意的是，本 demo 的禁写规则是写死的，用户若要更换切流范围则需自行计算禁写规则和下次路由规则，然后执行切流。

> 如果你打算停止体验，可进行：`cd appactive-demo` -> `docker-compose down`

更多信息，请见 [demo](details/demo_nacos.md)

## 开发指南
- [开发指南(Develop Guide)](details/developer_guide_nacos.md) ：介绍 AppActive 的使用方法，包括基本样例、支持插件、版本管理、使用效果等。


## 更新日志
- [更新日志查看（Change Log）](details/change_log.md) ：介绍 AppActive 的每个版本改动特性和内容。

## 贡献
我们总是欢迎新的贡献，无论是小的变动、重大的新功能还是其他提交，更多细节见[<如何贡献>](../en/contributing/contributing.md)。


## 联系
- 钉钉群（推荐）：34222602
- 一起建设更好的 AppActive https://www.wjx.cn/vj/P9FB5QR.aspx

## 用户登记

如果你已经或者打算在业务系统中使用AppActive，请让我们知道，你的使用对我们很重要：https://github.com/alibaba/Appactive/issues/10


