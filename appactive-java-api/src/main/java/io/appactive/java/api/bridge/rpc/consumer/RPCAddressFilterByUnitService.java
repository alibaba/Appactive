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

package io.appactive.java.api.bridge.rpc.consumer;

import java.util.List;


public interface RPCAddressFilterByUnitService<T> {

    void initAddressCallBack(RPCAddressCallBack<T> callBack);

    /**
     * init service list
     * @param servicePrimaryName primaryKey, dubbo: service+group+verison
     * @param list service remote ip list
     */
    void refreshAddressList(String providerAppName,String servicePrimaryName,List<T> list);

    /**
     * used with refreshAddressList， filter address
     */
    List<T> addressFilter(String providerAppName, String servicePrimaryName,String routeId);


    /**
     * 路由选址核心逻辑
     * @param providerAppName appName
     * @param servicePrimaryName 同机房优先的service 唯一标示，一般 springcloud 为 app+uri，hsf/dubbo 为 service+group+version
     * @param list 服务提供的列表
     *
     */
    List<T> addressFilter(String providerAppName, String servicePrimaryName, List<T> list,String routeId);

}
