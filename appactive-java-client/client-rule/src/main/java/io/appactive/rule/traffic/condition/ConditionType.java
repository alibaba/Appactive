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
     * A~B:number between A<= value >= B
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
