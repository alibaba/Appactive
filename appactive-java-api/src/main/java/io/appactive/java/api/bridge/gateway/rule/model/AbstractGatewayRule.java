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

package io.appactive.java.api.bridge.gateway.rule.model;

import java.util.List;
import java.util.Map;

import io.appactive.java.api.rule.traffic.bo.TrafficRule;

public abstract class AbstractGatewayRule extends TrafficRule {

    /**
     * 获得提取路由标的源
     * @return
     */
    public abstract List<String> getTokenSource();

    /**
     * 获得兜底的单元标
     * @return key 单元标 value 单元标的请求占比。所有单元标占比之和为100
     */
    public abstract Map<String, Double> getDefaultUnit();

}
