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

package io.appactive.rpc.springcloud.common.consumer.callback;

import com.netflix.loadbalancer.Server;
import io.appactive.java.api.bridge.rpc.constants.bo.RPCInvokerBO;
import io.appactive.java.api.bridge.rpc.consumer.RPCAddressCallBack;
import io.appactive.rpc.springcloud.common.consumer.ServerMeta;
import io.appactive.support.spi.SpiUtil;

import java.util.List;

public class SpringCloud2AddressCallBack<T> implements RPCAddressCallBack<T> {

    private static ServerMeta serverMeta = SpiUtil.loadFirstInstance(ServerMeta.class);

    @Override
    public String getMetaMapValue(T server, String key) {
        if (server == null){
            return null;
        }
        return getMetaMap(server,key);
    }

    @Override
    public String getServerToString(T server) {
        if (server == null){
            return null;
        }
        Server thisServer = getServer(server);
        return thisServer.getHost();
    }

    @Override
    public List<RPCInvokerBO<T>> changeToRPCInvokerBOList(List<T> servers) {
        return null;
    }

    @Override
    public List<T> changedToOriginalInvokerList(List<RPCInvokerBO<T>> RPCInvokerBOS) {
        return null;
    }

    private String getMetaMap(T server, String key) {
        Server thisServer = getServer(server);
        if (server == null){
            return null;
        }
        return serverMeta.getMetaMap(thisServer).get(key);
    }

    private Server getServer(T server){
        return (Server) server;
    }
}
