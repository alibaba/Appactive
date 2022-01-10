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

package io.appactive.rule.traffic.impl.file;

import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;

import io.appactive.channel.file.FileReadDataSource;
import io.appactive.java.api.channel.listener.DataListener;
import io.appactive.java.api.rule.traffic.ForbiddenRuleService;
import io.appactive.java.api.rule.traffic.TransformerRuleService;
import io.appactive.rule.traffic.bo.UnitMappingRuleBO;
import io.appactive.rule.traffic.condition.ConditionUtil;
import io.appactive.rule.traffic.condition.RuleCondition;
import io.appactive.rule.traffic.impl.file.base.BaseFileRuleService;
import io.appactive.rule.utils.FilePathUtil;
import io.appactive.support.log.LogUtil;

public class FileForbiddenRuleServiceImpl extends BaseFileRuleService implements ForbiddenRuleService {

    private TransformerRuleService transformerRuleService;

    @Override
    public boolean isRouteIdForbidden(String routeId) {
        if (memoryConditions == null){
            // not have rule,not forbidden
            return true;
        }
        String innerId = transformerRuleService.getRouteIdAfterTransformer(routeId);
        for (RuleCondition memoryCondition : memoryConditions) {
            if (memoryCondition.accept(innerId)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void setTransformerRuleService(TransformerRuleService transformerRuleService) {
        this.transformerRuleService = transformerRuleService;
    }

    public FileForbiddenRuleServiceImpl() {
        initFromFile(FilePathUtil.getForbiddenRulePath());
    }

    public FileForbiddenRuleServiceImpl(TransformerRuleService transformerRuleService) {
        this.transformerRuleService = transformerRuleService;
        initFromFile(FilePathUtil.getForbiddenRulePath());
    }

    public FileForbiddenRuleServiceImpl(String filePath, TransformerRuleService transformerRuleService) {
        this.transformerRuleService = transformerRuleService;
        initFromFile(filePath);
    }

    private List<RuleCondition> memoryConditions;


    private final DataListener<UnitMappingRuleBO> listener = new DataListener<UnitMappingRuleBO>() {
        @Override
        public String getListenerName() {
            return "ForbiddenRule-Listener-"+this.hashCode();
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
            Map<String, List<RuleCondition>> unitConditionMap = ConditionUtil.getUnitConditionMap(unitMappingRule);
            memoryConditions = unitConditionMap.values().iterator().next();
        }
    };

    private void initFromFile(String filePath) {
        FileReadDataSource<UnitMappingRuleBO> dataSource
            = getUnitMappingRuleReadDataSource(filePath);
        dataSource.addDataChangedListener(listener);
    }
}
