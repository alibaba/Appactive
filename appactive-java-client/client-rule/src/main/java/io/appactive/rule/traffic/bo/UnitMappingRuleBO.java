package io.appactive.rule.traffic.bo;

import java.util.List;
import java.util.Objects;

import io.appactive.java.api.rule.traffic.bo.UnitMappingRule;
import io.appactive.rule.traffic.bo.item.UnitMappingRuleItem;

/**
 *
 */
public class UnitMappingRuleBO extends UnitMappingRule {

    private String itemType;

    private List<UnitMappingRuleItem> items;

    public String getItemType() {
        return itemType;
    }

    public UnitMappingRuleBO setItemType(String itemType) {
        if (itemType != null) { this.itemType = itemType;}
        return this;
    }

    public List<UnitMappingRuleItem> getItems() {
        return items;
    }

    public UnitMappingRuleBO setItems(List<UnitMappingRuleItem> items) {
        if (items != null) { this.items = items;}
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        UnitMappingRuleBO that = (UnitMappingRuleBO)o;
        return Objects.equals(itemType, that.itemType) && Objects.equals(items, that.items);
    }

    @Override
    public int hashCode() {
        return Objects.hash(itemType, items);
    }
}
