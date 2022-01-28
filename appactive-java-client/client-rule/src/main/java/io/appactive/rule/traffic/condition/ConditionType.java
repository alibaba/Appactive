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

import java.util.HashMap;
import java.util.Map;

import io.appactive.rule.traffic.condition.impl.BetweenConditionImpl;

/**
 * 类PriorityLevel.java的实现描述：条件类型定义
 *
 */
public enum ConditionType {
    /**
     * A~B:number between A 	&lt;= value &gt;= B
     */
    Between("between", "@", BetweenConditionImpl.class);

    private String typeName;

    private String typeCode;

    private Class<? extends RuleCondition> conditionClass;

    /**
     * 状态上下文
     */
    private static final Map<String, ConditionType> CODE_CONTEXT = new HashMap<>();

    static {
        for (ConditionType ct : ConditionType.values()) {
            CODE_CONTEXT.put(ct.typeCode, ct);
        }
    }

    private ConditionType(String typeName, String typeCode, Class<? extends RuleCondition> conditionClass) {
        this.typeName = typeName;
        this.typeCode = typeCode;
        this.conditionClass = conditionClass;
    }


    public static ConditionType getByCode(String typeCode) {
        return CODE_CONTEXT.get(typeCode);
    }

    public boolean isTypeNameMatch(String typeName) {
        return this.typeName.equals(typeName);
    }

    public boolean isAssignableFrom(Class<? extends RuleCondition> conditionClass) {
        return this.conditionClass.isAssignableFrom(conditionClass);
    }

    /**
     * @return the typeName
     */
    public String getTypeName() {
        return typeName;
    }

    /**
     * @return the typeCode
     */
    public String getTypeCode() {
        return typeCode;
    }

    public Class<? extends RuleCondition> getConditionClass() {
        return conditionClass;
    }

}
