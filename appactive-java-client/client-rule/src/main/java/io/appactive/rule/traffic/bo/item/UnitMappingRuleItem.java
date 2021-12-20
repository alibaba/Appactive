package io.appactive.rule.traffic.bo.item;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 */
public class UnitMappingRuleItem {
    private String name;

    private List<Map<String, List<String>>> conditions;

    public String getName() {
        return name;
    }

    public UnitMappingRuleItem setName(String name) {
        if (name != null) { this.name = name;}
        return this;
    }

    public List<Map<String, List<String>>> getConditions() {
        return conditions;
    }

    public UnitMappingRuleItem setConditions(
        List<Map<String, List<String>>> conditions) {
        if (conditions != null) { this.conditions = conditions;}
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        UnitMappingRuleItem that = (UnitMappingRuleItem)o;
        return Objects.equals(name, that.name) && Objects.equals(conditions, that.conditions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, conditions);
    }
}
