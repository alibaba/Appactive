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
