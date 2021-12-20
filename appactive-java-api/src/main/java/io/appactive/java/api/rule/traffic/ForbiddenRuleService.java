package io.appactive.java.api.rule.traffic;

import io.appactive.java.api.base.extension.SPI;

@SPI
public interface ForbiddenRuleService {

    /**
     * 指定用户是否被禁
     *
     * @return
     */
    boolean isRouteIdForbidden(String routeId);

    void setTransformerRuleService(TransformerRuleService transformerRuleService);
}
