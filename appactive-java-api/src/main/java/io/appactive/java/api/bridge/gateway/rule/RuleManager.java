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

package io.appactive.java.api.bridge.gateway.rule;

import io.appactive.java.api.bridge.gateway.rule.model.AbstractGatewayRule;

public class RuleManager {

    /**
     * 获得业务类型下的规规则
     * 需要实现
     * @return
     */
    public AbstractGatewayRule getRule(){
        throw new UnsupportedOperationException();
    }

    /**
     * 设置业务类型的规则，新增 or 修改
     * 需要实现
     * @param rule 规则
     * @return 规则版本号
     */
    public String setRule( AbstractGatewayRule rule){
        throw new UnsupportedOperationException();
    }

}
