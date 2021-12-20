package io.appactive.java.api.rule.traffic;

import io.appactive.java.api.base.extension.SPI;


@SPI
public interface TrafficRouteRuleService {

    /**
     *
     * @return 单元名称-大写
     */
    String getUnitByRouteId(String routeId);

    boolean haveTrafficRule();

    void setTransformerRuleService(TransformerRuleService transformerRuleService);
}
