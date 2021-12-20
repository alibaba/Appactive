package io.appactive.rule.traffic.condition;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import io.appactive.java.api.base.exception.ExceptionFactory;
import io.appactive.rule.traffic.bo.UnitMappingRuleBO;
import io.appactive.rule.traffic.bo.item.UnitMappingRuleItem;

public class ConditionUtil {

    public static Map<String, List<RuleCondition>> getUnitConditionMap(UnitMappingRuleBO unitMappingRule) {
        Map<String, List<RuleCondition>> unitConditionMap = new HashMap<>();

        List<UnitMappingRuleItem> items = unitMappingRule.getItems();
        for (UnitMappingRuleItem item : items) {
            String unitFlag = item.getName();
            List<Map<String, List<String>>> conditions = item.getConditions();
            List<RuleCondition> conditionList = ConditionUtil.initConditionRuleToConditionList(conditions);
            // 条件按优先级排序
            Comparator<RuleCondition> comparator = Comparator.comparingInt(RuleCondition::priority);
            conditionList.sort(comparator);
            unitConditionMap.put(unitFlag.toUpperCase(),conditionList);
        }
        return unitConditionMap;
    }

    public static List<RuleCondition> initConditionRuleToConditionList(List<Map<String, List<String>>> conditions) {
        List<RuleCondition> conditionList = new ArrayList<>();

        int conditionIndex = 0;
        for (Map<String, List<String>> mappingConditionMap : conditions) {
            Set<Entry<String, List<String>>> entries = mappingConditionMap.entrySet();
            for (Entry<String, List<String>> entry : entries) {
                String conditionKey = entry.getKey();
                List<String> routeIdMappingStrList = entry.getValue();
                String conditionCode = conditionKey.substring(0, 1);
                String routeTokenName = conditionKey.substring(1);
                RuleCondition condition = getRuleCondition(conditionKey, conditionCode);
                condition.init(conditionIndex, routeTokenName, routeIdMappingStrList);

                conditionList.add(condition);
                conditionIndex++;
            }
        }
        return conditionList;
    }

    private static RuleCondition getRuleCondition(String conditionKey, String conditionCode) {
        RuleCondition condition = null;

        ConditionType conditionType = ConditionType.getByCode(conditionCode);
        if (conditionType == null) {
            String msg = "ConditionType[" + conditionKey + "] is invalid";
            throw ExceptionFactory.makeFault(msg);
        }
        Class<? extends RuleCondition> conditionClass = conditionType.getConditionClass();
        try {
            condition = conditionClass.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return condition;
    }
}
