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

package io.appactive.rule.traffic.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.alibaba.fastjson.JSON;

import com.alibaba.fastjson.TypeReference;
import io.appactive.java.api.channel.ConfigReadDataSource;
import io.appactive.java.api.channel.ConverterInterface;
import io.appactive.java.api.channel.listener.DataListener;
import io.appactive.java.api.rule.RuleTypeEnum;
import io.appactive.java.api.rule.traffic.TrafficRouteRuleService;
import io.appactive.java.api.rule.traffic.TransformerRuleService;
import io.appactive.rule.ClientRuleService;
import io.appactive.rule.traffic.bo.TransformerRuleBO;
import io.appactive.rule.traffic.bo.UnitMappingRuleBO;
import io.appactive.rule.traffic.condition.ConditionUtil;
import io.appactive.rule.traffic.condition.RuleCondition;
import io.appactive.rule.traffic.impl.base.BaseRuleService;
import io.appactive.support.log.LogUtil;

public class TrafficRouteRuleServiceImpl extends BaseRuleService implements TrafficRouteRuleService {

    private TransformerRuleService transformerRuleService;

    private List<TrafficCondition> trafficConditionList = new ArrayList<>();


    @Override
    public String getUnitByRouteId(String routeId) {
        String innerId = transformerRuleService.getRouteIdAfterTransformer(routeId);

        for (TrafficCondition trafficCondition : trafficConditionList) {
            boolean accept = trafficCondition.getCondition().accept(innerId);
            if (accept){
                return trafficCondition.getUnitFlag();
            }
        }
        return null;
    }

    @Override
    public boolean haveTrafficRule() {
        return !trafficConditionList.isEmpty();
    }

    @Override
    public void setTransformerRuleService(TransformerRuleService transformerRuleService) {
        this.transformerRuleService = transformerRuleService;
    }

    public TrafficRouteRuleServiceImpl() {
        initFromUri(ClientRuleService.getDefaultUri(RuleTypeEnum.trafficRouteRule));
    }

    public TrafficRouteRuleServiceImpl(TransformerRuleService transformerRuleService) {
        this.transformerRuleService = transformerRuleService;
        initFromUri(ClientRuleService.getDefaultUri(RuleTypeEnum.trafficRouteRule));
    }

    public TrafficRouteRuleServiceImpl(String uri, TransformerRuleService transformerRuleService) {
        this.transformerRuleService = transformerRuleService;
        initFromUri(uri);
    }



    private final DataListener<UnitMappingRuleBO> listener = new DataListener<UnitMappingRuleBO>() {
        @Override
        public String getListenerName() {
            return "TrafficRouteRule-Listener-"+this.hashCode();
        }

        @Override
        public void dataChanged(UnitMappingRuleBO old,UnitMappingRuleBO unitMappingRule) {
            if (!checkRuleRight(unitMappingRule)) {
                LogUtil.error("forbidden rule error,not change memory value,data:"+ JSON.toJSONString(unitMappingRule));
                return;
            }
            if (unitMappingRule == null){
                return;
            }
            List<TrafficCondition> trafficConditions = new ArrayList<>();
            Map<String, List<RuleCondition>> unitConditionMap = ConditionUtil.getUnitConditionMap(unitMappingRule);
            for (Entry<String, List<RuleCondition>> entry : unitConditionMap.entrySet()) {
                String unitFlag = entry.getKey().toUpperCase();
                List<RuleCondition> ruleConditions = entry.getValue();
                for (RuleCondition ruleCondition : ruleConditions) {
                    int priority = ruleCondition.priority();
                    TrafficCondition trafficCondition = new TrafficCondition(unitFlag,ruleCondition,priority);
                    trafficConditions.add(trafficCondition);
                }
            }

            Comparator<TrafficCondition> comparator = Comparator.comparingInt(TrafficCondition::getPriority);
            trafficConditions.sort(comparator);
            trafficConditionList = trafficConditions;
        }
    };

    private void initFromUri(String filePath) {
        ConfigReadDataSource<UnitMappingRuleBO> dataSource = getUnitMappingRuleReadDataSource(filePath);
        dataSource.addDataChangedListener(listener);
    }



    class TrafficCondition {
        private String unitFlag;
        private RuleCondition condition;
        private int priority;

        public TrafficCondition(String unitFlag, RuleCondition condition, int priority) {
            this.unitFlag = unitFlag;
            this.condition = condition;
            this.priority = priority;
        }

        public String getUnitFlag() {
            return unitFlag;
        }

        public RuleCondition getCondition() {
            return condition;
        }

        public int getPriority() {
            return priority;
        }
    }
}
