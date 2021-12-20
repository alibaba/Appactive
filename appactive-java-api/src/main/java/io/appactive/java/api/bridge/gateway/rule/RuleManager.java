package io.appactive.java.api.bridge.gateway.rule;

import io.appactive.java.api.bridge.gateway.rule.model.AbstractGatewayRule;

public class RuleManager {

    /**
     * 获得业务类型下的规规则
     * 需要实现
     * @return
     */
    public AbstractGatewayRule getRule(){
        throw new UnsupportedOperationException();
    }

    /**
     * 设置业务类型的规则，新增 or 修改
     * 需要实现
     * @param rule 规则
     * @return 规则版本号
     */
    public String setRule( AbstractGatewayRule rule){
        throw new UnsupportedOperationException();
    }

}
