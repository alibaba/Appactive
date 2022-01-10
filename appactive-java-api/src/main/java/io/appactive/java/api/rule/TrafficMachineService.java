/*
 * Copyright 1999-2022 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
