package io.appactive.java.api.rule.traffic;

/**
 */
public interface TransformerRuleService {

    /**
     * trans routeId to target routeId
     * @return target routeId
     */
    String getRouteIdAfterTransformer(String routeId);
}
