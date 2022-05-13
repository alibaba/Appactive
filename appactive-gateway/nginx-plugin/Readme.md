---
layout: default
parent: CNReadme
---
# AppActive Gateway 实现
本文给出的实现案例可以基于 Tengine。也可以基于官方 Nginx，但是需要自行编译相关模块，主要是 Lua。也可以直接基于 OpenResty。

## 主要功能
对于任意一种实现的多活网关，都需要实现下面的三大主要功能，才能完整的实现多活路由功能。

### 流量划分
本部份分为两个步骤。

#### 路由标提取
对于 http 请求来说，我们可以从 header、cookie、arg 中提取路由标。
进一步地支持按顺序进行提取，比如先从 header 中提取，如果 header 中没有，再从 cookie 中提取，直到取到路由标。

#### 路由标转换
拿到路由标后，并不一定就直接使用，比如我们提取到了完整的手机号，但是可能仅关心尾号，诸如此类。
所以我们定义了转换规则：

- 取模
- 待扩展

### server 划分
本部份有两种做法，本案例采用第一种，但是第二种也介绍下。

#### 配置文件
将不同的 server 放到不同的 upstream 中，并将其标签放到 upstream 的名字中。
这种方案下，选择 server 时只需要设置 `proxy_pass $upstream` 即可。

- 优点是简单，且能利用 nginx 自己的负载均衡策略（如果 upstream 内部有多个 server）。
- 缺点是 server 变化时需要 nginx reload。当然，如果server变化不频繁（比如是 vip）则这种方式性价比较高。

#### 动态管理
将不同的 server 放到 nginx 共享内存中，然后每个 worker 进程从共享内存中获取所有 server 及其标签。
这种方案下，选择 server 时只需在一个固定的 upstream 中运行 Lua 脚本，脚本中使用 set_current_peer 确定最终的 server。
此外，还需要一个在 worker 和共享内存中同步 server 的策略，比如轮询、懒加载等。

- 优点是在 server 变化时不需要 nginx reload，自定义能力较强。
- 缺点是需要自行维护 server 列表，并且在同一个标签有多个 server 时需要自行实现负载均衡策略

### 流量与 server 的映射规则
本部份分为两个步骤

#### 多活路由

1. 按规则顺序逐个提取路由标，提取到则停止本步骤
2. 按规则转换路由标
3. 遍历所有单元标的条件，若有多个条件，返回首先命中的单元标
4. 将流量转发给该单元标下的server

#### 兜底
当出现提取不到路由标、路由标没有归属单元标、计算出错等场景时，不能暴力的返回错误，
而是应该利用我们提前定义好的兜底策略，将流量路由到特定的server去，比如本单元的server

## 辅助功能
对于任意一种实现的多活网关，可以选择性地实现下面的一些辅助功能，在完成多活路由之外，可以从功能齐全度、稳定性等方面提升多活网关。

### 路由标透传
当我们提取到路由标时，可以将路由标植入转发给下游的流量中，这样下游也能这个路由标做一些事情，
比如服务调用策略选择、链路追踪、数据策略等等。

### 改造兼容
业务在向着异地多活改造的过程中，不是一蹴而就的，我们要允许一部分业务先改造完成，另一部分业务慢慢改造。
这样多活网关就需要支持两种路由策略，一是多活路由，二是中心化路由（也就是改造前），并且允许业务方在配置的时候进行选择。

# demo 说明
为了简洁起见，本 demo 基于 docker，用户在安装了 docker 的前提下，可以运行以下命令来运行 demo ：

```
docker build --build-arg UNITFLAG=hangzhou -t app-active/gateway:0.3 .

# 注意 若要单独启动gateway 则需要 将 example.conf 中的 frontend 换成 ip 
docker run -d --name gateway --hostname gateway -p 80:80 -p 8090:8090 app-active/gateway:0.3

# 进入容器 可选
docker exec -it gateway bash
```

然后运行以下命令来测试 demo ：

```
curl --header "Content-Type: application/json" \
--request POST \
--data '{"key":"459236fc-ed71-4bc4-b46c-69fc60d31f18_test1122","value":{"idSource":{"source":"arg","tokenKey":"r_id"},"idTransformer":[{"id":"userIdBetween","mod":"10000"}],"idUnitMapping":{"items":[{"conditions":[{"@userIdBetween":["0~1999"]}],"name":"center"},{"conditions":[{"@userIdBetween":["2000~9999"]}],"name":"unit"}],"itemType":"UnitRuleItem"}}}' \
127.0.0.1:8090/set

curl -H "Host:demo.appactive.io" "127.0.0.1/demo" -H "r_id:12"
# 结果如下
{"code":200,"msg":"param is nil; unit_type is test1122; unit_key is 12; unit is center"}

# 可以通过修改 路由规则 和 路由标 来观察结果的正确性

```
