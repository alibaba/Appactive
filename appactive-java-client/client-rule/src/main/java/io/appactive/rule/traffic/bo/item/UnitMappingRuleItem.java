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
