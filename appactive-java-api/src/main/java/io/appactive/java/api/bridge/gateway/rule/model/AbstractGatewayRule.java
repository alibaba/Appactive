package io.appactive.java.api.bridge.gateway.rule.model;

import java.util.List;
import java.util.Map;

import io.appactive.java.api.rule.traffic.bo.TrafficRule;

public abstract class AbstractGatewayRule extends TrafficRule {

    /**
     * 获得提取路由标的源
     * @return
     */
    public abstract List<String> getTokenSource();

    /**
     * 获得兜底的单元标
     * @return key 单元标 value 单元标的请求占比。所有单元标占比之和为100
     */
    public abstract Map<String, Double> getDefaultUnit();

}
