package io.appactive.rule.traffic.condition;

import java.util.List;


public interface RuleCondition {
    /**
     * 条件类型代码
     *
     * @return
     */
    String conditionType();

    /**
     * 当前条件使用的tokenName
     *
     * @return
     */
    String tokenName();

    /**
     * 条件优先级
     *
     * @return
     */
    int priority();

    /**
     * 初始化条件
     *
     * @param conditionIndex 优先级
     * @param routeTokenName 路由令牌名称
     */
    void init(int conditionIndex, String routeTokenName, List<String> valueList);

    /**
     * token是否符合本条件
     *
     * @return
     */
    boolean accept(String innerId);
}
