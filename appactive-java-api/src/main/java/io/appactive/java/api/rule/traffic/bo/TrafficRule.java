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

package io.appactive.java.api.rule.traffic.bo;

import java.util.List;

/**
 */
public class TrafficRule <TransformerRuleT extends TransformerRule, UnitMappingRuleT extends UnitMappingRule>{

    private String routerType;

    /**
     * route define
     */
    private List<TransformerRuleT> transformerRuleList;

    private List<UnitMappingRuleT> unitMappingRuleList;

    public String getRouterType() {
        return routerType;
    }

    public TrafficRule setRouterType(String routerType) {
        if (routerType != null) { this.routerType = routerType;}
        return this;
    }

    public List<TransformerRuleT> getTransformerRuleList() {
        return transformerRuleList;
    }

    public TrafficRule setTransformerRuleList(List<TransformerRuleT> transformerRuleList) {
        if (transformerRuleList != null) { this.transformerRuleList = transformerRuleList;}
        return this;
    }

    public List<UnitMappingRuleT> getUnitMappingRuleList() {
        return unitMappingRuleList;
    }

    public TrafficRule setUnitMappingRuleList(List<UnitMappingRuleT> unitMappingRuleList) {
        if (unitMappingRuleList != null) { this.unitMappingRuleList = unitMappingRuleList;}
        return this;
    }
}
