package io.appactive.rule;

import io.appactive.java.api.rule.machine.AbstractMachineUnitRuleService;
import io.appactive.java.api.rule.property.db.DataScopeRuleService;
import io.appactive.java.api.rule.traffic.ForbiddenRuleService;
import io.appactive.java.api.rule.traffic.TrafficRouteRuleService;
import io.appactive.java.api.rule.traffic.TransformerRuleService;
import io.appactive.support.spi.SpiUtil;

public class ClientRuleService {

    private static final DataScopeRuleService dataScopeRuleService = SpiUtil.loadFirstInstance(DataScopeRuleService.class);
    private static final AbstractMachineUnitRuleService machineUnitRuleService = SpiUtil.loadFirstInstance(AbstractMachineUnitRuleService.class);
    private static final TransformerRuleService transformerRuleService = SpiUtil.loadFirstInstance(TransformerRuleService.class);
    private static final TrafficRouteRuleService trafficRouteRuleService = SpiUtil.loadFirstInstance(TrafficRouteRuleService.class);
    private static final ForbiddenRuleService forbiddenRuleService = SpiUtil.loadFirstInstance(ForbiddenRuleService.class);

    static {
        trafficRouteRuleService.setTransformerRuleService(transformerRuleService);
        forbiddenRuleService.setTransformerRuleService(transformerRuleService);
    }
    public static DataScopeRuleService getDataScopeRuleService() {
        return dataScopeRuleService;
    }

    public static AbstractMachineUnitRuleService getMachineUnitRuleService() {
        return machineUnitRuleService;
    }

    public static TrafficRouteRuleService getTrafficRouteRuleService() {
        return trafficRouteRuleService;
    }

    public static ForbiddenRuleService getForbiddenRuleService() {
        return forbiddenRuleService;
    }
}
