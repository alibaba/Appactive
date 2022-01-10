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
