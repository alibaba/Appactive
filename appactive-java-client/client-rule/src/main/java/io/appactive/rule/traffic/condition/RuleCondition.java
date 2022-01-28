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

package io.appactive.rule.traffic.condition;

import java.util.List;


public interface RuleCondition {
    /**
     * 条件类型代码
     *
     * @return conditionType
     */
    String conditionType();

    /**
     * 当前条件使用的tokenName
     *
     * @return tokenName
     */
    String tokenName();

    /**
     * 条件优先级
     *
     * @return priority
     */
    int priority();

    /**
     * 初始化条件
     *
     * @param conditionIndex 优先级
     * @param routeTokenName 路由令牌名称
     * @param valueList specific condition value
     */
    void init(int conditionIndex, String routeTokenName, List<String> valueList);

    /**
     * token是否符合本条件
     * @param innerId as it is
     * @return innerId meets the criteria or not
     */
    boolean accept(String innerId);
}
