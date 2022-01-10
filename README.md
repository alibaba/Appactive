
<div style="text-align: center">
   <img src="https://appactive.oss-cn-beijing.aliyuncs.com/images/appactive-logo.jpg?x-oss-process=style/h400" />
</div>

# A middleware to build an application with multiple active architectures（阿里巴巴开源的一款标准通用且功能强大的构建应用多活架构的开源中间件）

[![License](https://img.shields.io/badge/license-Apache%202-4EB1BA.svg)](https://www.apache.org/licenses/LICENSE-2.0.html)

English | [中文](docs/cn/README_CN.md)


---

## Introduction

AppActive，is an open source middleware that builds a cloud-native, high-availability and multi-active disaster tolerance
architecture for business applications. It provides standards, implementations, and demos for applying the multi-active
disaster recovery architecture, which is suitable for rich business scenarios (single AZ, single region, single cloud,
multi AZ, multi region, multi-cloud, self-built IDC...).

AppActive is an internal open source project of AHAS-MSHA.It is based on Alibaba's nearly 9-year production disaster
recovery practice, and combines the best practices of many external customers and communities from Alibaba Cloud's
commercial services, and has the characteristics of high reliability and scalability.

## Architecture

![appactive_landscape](https://appactive.oss-cn-beijing.aliyuncs.com/images/appa_landscape.jpg?x-oss-process=style/h600)

The overall structure of AppActive covers the data plane and the control plane。It manages application traffic and data
in the whole process. As shown in the figure above, the details can be found in the following links:

- [Architecture](docs/en/details/architecture.md): Introduce AppActive deployment architecture and technical
  architecture.
- [Concept](docs/en/details/concept.md): Introduce the basic conceptual model of AppActive.
- [Features](docs/en/details/features.md): Introduce the features implemented by AppActive.

## Quick Start

### Prerequisite

The following software are assumed installed:
- docker && docker-compose
- curl

note：this demo contains many applications，please adjust your memory settings to avoid failures。

### Experience
![appactive_landscape](https://appactive.oss-cn-beijing.aliyuncs.com/images/AppActive-demo.png?x-oss-process=style/h200)

#### Docker Run
1. `appactive-gateway`: `cd appactive-gateway/nginx-plugin` -> `docker build --build-arg UNITFLAG=center -t app-active/gateway:1.0-SNAPSHOT .`
2. `appactive`: maven build to get the all the jar packages: `cd ../../` -> `mvn clean package -Dmaven.test.skip -U`
3. `appactive-demo`: `cd appactive-demo` -> Run `sh run.sh` to start all applications
4. `appactive-portal`: `cd ../appactive-portal` -> Run `sh baseline.sh` to push the baseline
5. Bind the local host: `127.0.0.1 demo.appactive.io`, visit the browser `http://demo.appactive.io/listProduct?r_id=1999` to
   see the effect
6. `appactive-portal`: Run `cut.sh` to cut the stream. This demo supports two cutting methods: ratio and range
    - Commands: `sh cut.sh`.
    - Note： the rules of the demo are hard-coded. If you want to change the cut flow range, you need to calculate the
      rules by yourself, and then execute the cut flow.

> If you plan to stop the experience, you can proceed: `cd appactive-demo` -> `docker-compose down`

#### More
For more information, please see [demo](docs/en/details/demo.md)

## Developer Guide

- [DEVELOPER Guide](docs/en/details/developer_guide.md) ：Introduce how to use AppActive, including basic samples,
  support plug-ins, version management, use effects.

## Change Log

- [Change Log](docs/en/details/change_log.md) ：Introduce the change features and content of each version of AppActive.

## Contributing

We always welcome new contributions, whether for trivial cleanups, big new features or other material rewards, more
details see [here](docs/en/contributing/contributing.md)

## Connect

- DingTalk Group：34222602

----------

## License

[Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.html) Copyright (C) Apache Software Foundation

