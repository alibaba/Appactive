/*
 * Copyright 1999-2022 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.appactive.java.api.bridge.rpc.callback;

import java.util.List;

import io.appactive.java.api.bridge.rpc.constants.bo.RPCInvokerBO;


public interface RPCServiceCallBack<T> {

    /**
     * 获得当前 server 的 标签map
     * @param server rpc 框架的server 对象，要查询的 key
     * @return value
     */
    String getMetaMapValue(T server,String key);

    /**
     * server 的 string 内容，用于打印和唯一使用
     */
    String getServerToString(T server);

    /**
     * 原始地址变更为 优先需要的统一地址
     */
    List<RPCInvokerBO<T>> changeToRPCInvokerBOList(List<T> servers);

    /**
     * 优先地址更改回原始地址
     */
    List<T> changedToOriginalInvokerList(List<RPCInvokerBO<T>> rpcInvokerBOS);
}
