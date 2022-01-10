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

package io.appactive.db.mysql.constants;

public interface MysqlConstant {
    String INSTANCE_ID_KEY = "activeInstanceId";

    String DB_NAME_KEY = "activeDbName";

    String PORT_NAME_KEY = "activePort";

    String ERROR_ROUTE_FLOW_ROUTER_NOT_HAVE_ROUTER_ID =
        "the route flow routerId is null is forbidden by db unit.";
}
