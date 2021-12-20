package io.appactive.java.api.rule;

import io.appactive.java.api.rule.machine.AbstractMachineUnitRuleService;
import io.appactive.java.api.rule.traffic.TrafficRouteRuleService;
import io.appactive.java.api.utils.lang.StringUtils;

public class TrafficMachineService {

    private TrafficRouteRuleService trafficRouteRuleService;

    private AbstractMachineUnitRuleService abstractMachineUnitRuleService;

    public TrafficMachineService(TrafficRouteRuleService trafficRouteRuleService,
                                 AbstractMachineUnitRuleService abstractMachineUnitRuleService) {
        this.trafficRouteRuleService = trafficRouteRuleService;
        this.abstractMachineUnitRuleService = abstractMachineUnitRuleService;
    }

    /**
     * 当前用户是否在当前单元类型下的单元为本单元，仅供外部业务系统使用
     */
    public boolean isInCurrentUnit(String routeId) {
        String unitByRouteId = trafficRouteRuleService.getUnitByRouteId(routeId);
        String currentUnit = abstractMachineUnitRuleService.getCurrentUnit();
        return StringUtils.isNotBlank(currentUnit) && StringUtils.isNotBlank(unitByRouteId) && unitByRouteId
            .equalsIgnoreCase(currentUnit);

    }
}
