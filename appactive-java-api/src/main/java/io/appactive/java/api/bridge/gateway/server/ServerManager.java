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

package io.appactive.java.api.bridge.gateway.server;

import java.util.List;

public class ServerManager {

    /**
     * 获得 某单元标下的所有server ip
     * 需要实现
     * @param unitFlag as it is
     * @return server ip list
     */
    public List<String> getServerList( String unitFlag) {
        throw new UnsupportedOperationException();
    }

    /**
     * 设置 server ip
     * 需要实现
     * @param unitFlag as it is
     * @param serverList as it is
     * @return server
     */
    public String setServerList(String unitFlag, List<String> serverList) {
        throw new UnsupportedOperationException();
    }

}
