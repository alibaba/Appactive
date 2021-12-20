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
