
# AppActive

---
## 基本介绍
AppActive，是一个面向业务应用构建云原生高可用多活容灾架构的开源中间件。它提供了应用多活容灾架构的标准、实现和 Demo，适用于丰富的业务场景（单 AZ、单 Region、单云、多 AZ、多 Region、多云、自建 IDC等）。

AppActive 建立在 阿里巴巴 使用 AHAS-MSHA 系统大规模运行生产应用系统的8年经验之上，且结合了来自阿里云商业化服务的外部多家客户和社区的最佳实践，具备高可靠、可拓展等特性。

## 快速体验
![appactive_landscape](https://appactive.oss-cn-beijing.aliyuncs.com/images/AppActive-demo.png)

### 前提
本 demo 要求安装如下软件
- docker && docker-compose
- curl

注：本 demo 包含较多应用，请调大 docker 内存，避免闪退。

### 步骤

1. 进入 `appactive-gateway` 模块:`cd appactive-gateway/nginx-plugin` -> `docker build --build-arg UNITFLAG=center -t app-active/gateway:1.0-SNAPSHOT .`
2. 进入 `appactive` 模块: maven build 获得所有 jar 包`cd ../../` -> `mvn clean package -Dmaven.test.skip -U`
3. 在 `appactive-demo` 模块:`cd appactive-demo` -> 运行 `sh run.sh` ，启动所有应用
4. 运行 `appactive-portal` 模块:`cd ../appactive-portal` -> `sh baseline.sh`，推送基线
5. 绑定本地 host: `127.0.0.1 demo.appactive.io`，浏览器访问 `http://demo.appactive.io/listProduct?r_id=1999` 查看效果
6. 运行 `appactive-portal` 模块中的 `cut.sh` 进行切流。本 demo 支持两种切流方式：精准和范围，
   - 命令：`sh cut.sh`。
   - 注意: demo 的规则是写死的，你若要更换切流范围则需自行计算规则，然后执行切流。
   
> 如果你打算停止体验，可进行：`cd appactive-demo` -> `docker-compose down`

更多信息，请见 [demo](details/demo.md)

## 概念&设计
![appactive_landscape](https://appactive.oss-cn-beijing.aliyuncs.com/images/appactive_landscape.jpg)

AppActive 整体架构覆盖数据面和管控面，全流程管理应用流量和数据，如上图，详细内容可见下述的链接：

- [架构(Architecture)](details/architecture.md)：介绍 Appactive 部署架构和技术架构。
- [概念(Concept)](details/concept.md)：介绍 Appactive 的基本概念模型。
- [特性(Features)](details/features.md)：介绍 Appactive 实现的功能特性。


## 开发指南
- [开发指南(Develop Guide)](details/developer_guide.md) ：介绍 AppActive 的使用方法，包括基本样例、支持插件、版本管理、使用效果等。


## 更新日志
- [更新日志查看（Change Log）](details/change_log.md) ：介绍 AppActive 的每个版本改动特性和内容。

## 贡献
我们总是欢迎新的贡献，无论是小的变动、重大的新功能还是其他提交，更多细节见[<如何贡献>](../en/contributing/contributing.md)。


## 联系
- 钉钉群（推荐）：34222602

## License
[Apache License, Version 2.0](https://www.apache.org/licenses/LICENSE-2.0.html) Copyright (C) Apache Software Foundation

