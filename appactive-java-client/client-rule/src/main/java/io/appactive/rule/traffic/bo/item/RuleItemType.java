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

package io.appactive.rule.traffic.bo.item;

import java.util.HashMap;
import java.util.Map;

import io.appactive.java.api.base.enums.IEnum;


public enum RuleItemType implements IEnum<String> {
    /**
     * 单元化规则项
     */
    UnitRuleItem("UnitRuleItem"),

    /**
     * 禁止规则项
     */
    ForbiddenRuleItem("ForbiddenRuleItem");

    private final String itemType;

    /**
     * 规则项上下文
     */
    private static final Map<String, RuleItemType> CONTEXT = new HashMap<>();

    static {
        for (RuleItemType itemType : RuleItemType.values()) {
            CONTEXT.put(itemType.itemType, itemType);
        }
    }

    RuleItemType(String itemType) {
        this.itemType = itemType;
    }

    public static RuleItemType get(String itemType) {
        return CONTEXT.get(itemType);
    }

    @Override
    public String getValue() {
        return itemType;
    }

    public String getItemType() {
        return itemType;
    }

}
