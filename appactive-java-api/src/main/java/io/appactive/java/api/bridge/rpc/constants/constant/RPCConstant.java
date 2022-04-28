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

package io.appactive.java.api.bridge.rpc.constants.constant;


public interface RPCConstant {



    /**
     * auto by machine
     * url：unit label
     */
    String URL_UNIT_LABEL_KEY = "ut";

    /**
     * manual
     * url: resource active
     * for example: CENTER_RESOURCE_TYPE/UNIT_RESOURCE_TYPE/NORMAL_RESOURCE_TYPE
     */
    String URL_RESOURCE_ACTIVE_LABEL_KEY = "ra";

    String URL_RESOURCE_ACTIVE_LABEL = "rsActive";

    /**
     * manual(optional)
     * url: route index key
     * get route Id by params, if notExit, use thread
     */
    String URL_ROUTE_INDEX_KEY = "ri";
    String URL_ROUTE_INDEX = "routeIndex";

    String CONSUMER_REMOTE_ROUTE_ID_KEY = "_unit_rpc_route_id";

    String SPRING_CLOUD_SERVICE_META = "sc_svc_meta";
    String SPRING_CLOUD_SERVICE_META_VERSION = "sc_svc_meta_version";

}
