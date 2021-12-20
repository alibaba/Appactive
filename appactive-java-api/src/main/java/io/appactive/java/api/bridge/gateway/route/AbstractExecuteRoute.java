package io.appactive.java.api.bridge.gateway.route;

import io.appactive.java.api.bridge.gateway.rule.RuleManager;
import io.appactive.java.api.bridge.gateway.rule.model.AbstractGatewayRule;
import io.appactive.java.api.bridge.gateway.server.ServerManager;

import java.util.List;

public abstract class AbstractExecuteRoute {

    protected RuleManager ruleManager = new RuleManager();
    protected ServerManager serverManager = new ServerManager();

    /**
     * 自行设置规则管理器
     */
    abstract void setRuleManager();

    /**
     * 自行设置server管理器
     */
    abstract void setServerManager();

    /**
     * 从请求中获得路由标
     * @param sourceList 源
     * @param tokenDefines 解析规则
     * @return
     */
    abstract String getRouterIdFromRequest(List<String> sourceList, List<?> tokenDefines);

    /**
     * 从路由标和映射关系 获得该请求的归属单元标
     * @param routerId 路由标
     * @param judgeRules 映射关系
     * @return
     */
    abstract String getUnitFlagFromRequest(String routerId, List<?> judgeRules);


    /**
     * 执行负载均衡策略，路由请求
     * @param serverList
     * @return
     */
    abstract String balanceAndRoute(List<String> serverList);


    /**
     * 执行多活路由
     */
    public final void call(){
        AbstractGatewayRule rule = ruleManager.getRule();
        String routerId = getRouterIdFromRequest(rule.getTokenSource(), rule.getTransformerRuleList());
        String unitFlag = getUnitFlagFromRequest(routerId, rule.getUnitMappingRuleList());
        List<String> serverList = serverManager.getServerList(unitFlag);
        balanceAndRoute(serverList);
    }
}
