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

package io.appactive.rule;

import io.appactive.java.api.rule.machine.AbstractMachineUnitRuleService;
import io.appactive.java.api.rule.property.db.DataScopeRuleService;
import io.appactive.java.api.rule.traffic.ForbiddenRuleService;
import io.appactive.java.api.rule.traffic.IdSourceRuleService;
import io.appactive.java.api.rule.traffic.TrafficRouteRuleService;
import io.appactive.java.api.rule.traffic.TransformerRuleService;
import io.appactive.support.spi.SpiUtil;

public class ClientRuleService {

    private static final DataScopeRuleService dataScopeRuleService = SpiUtil.loadFirstInstance(DataScopeRuleService.class);
    private static final AbstractMachineUnitRuleService machineUnitRuleService = SpiUtil.loadFirstInstance(AbstractMachineUnitRuleService.class);
    private static final TransformerRuleService transformerRuleService = SpiUtil.loadFirstInstance(TransformerRuleService.class);
    private static final TrafficRouteRuleService trafficRouteRuleService = SpiUtil.loadFirstInstance(TrafficRouteRuleService.class);
    private static final ForbiddenRuleService forbiddenRuleService = SpiUtil.loadFirstInstance(ForbiddenRuleService.class);
    private static final IdSourceRuleService idSourceRuleService = SpiUtil.loadFirstInstance(IdSourceRuleService.class);

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

    public static IdSourceRuleService getIdSourceRuleService() {
        return idSourceRuleService;
    }
}
