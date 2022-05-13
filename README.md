---
layout: default
nav_exclude: true
---


<div style="text-align: center">
   <img src="https://appactive.oss-cn-beijing.aliyuncs.com/images/appactive-logo.jpg?x-oss-process=style/h400" />
</div>

# A middleware dedicate to building multi-site active/active application architectures

> A middleware designed by alibaba: standard, universal and powerful

[![License](https://img.shields.io/badge/license-Apache%202-4EB1BA.svg)](https://www.apache.org/licenses/LICENSE-2.0.html)
[![](https://img.shields.io/badge/Maven%20Central-v0.2-blue.svg)](https://mvnrepository.com/search?q=appactive)

English | [中文](docs/cn/README_CN.md)
---

## Introduction

AppActive is an open source middleware that builds a cloud-native, high-availability and multi-active disaster tolerance
architecture for business applications. It provides standards, implementations, and demos for applying the multi-active
disaster recovery architecture, which is suitable for rich business scenarios (single AZ, single region, single cloud,
multi AZ, multi region, multi-cloud, self-built IDC...).

AppActive is an internal open source project of AHAS-MSHA. It is based on Alibaba's nearly 9-year production disaster
recovery practice, and combines the best practices of many external customers and communities from Alibaba Cloud's
commercial services, and has the characteristics of high reliability and scalability.

## Architecture

![appactive_landscape](https://appactive.oss-cn-beijing.aliyuncs.com/images/aactive_landscape.jpg?x-oss-process=style/h600)

The overall structure of AppActive covers the data plane and the control plane. It manages application traffic and data
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

note：this demo contains many applications, please adjust your memory settings to avoid failures.

### Experience
![appactive_landscape](https://appactive.oss-cn-beijing.aliyuncs.com/images/demo.png?x-oss-process=style/h200)

#### Docker Run

1. Run `sh run.sh` in the `appactive-demo` module to start all applications
2. Bind hosts: `127.0.0.1 demo.appactive.io`, and then visit `http://demo.appactive.io/buyProduct?r_id=2000` to see how it works
3. Run `sh cut.sh` in the `appactive-portal` module to switch flow. It should be noted that the writing-forbidden rules of this demo are hard-coded. If you want to change the range, you need to calculate the writing-forbidden rules and the next-routing rules, and then execute the flow switch.

> If you plan to stop the experience, you can proceed: `cd appactive-demo` -> `docker-compose down`

#### More
For more, please visit [demo](docs/en/details/demo_nacos.md)

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
