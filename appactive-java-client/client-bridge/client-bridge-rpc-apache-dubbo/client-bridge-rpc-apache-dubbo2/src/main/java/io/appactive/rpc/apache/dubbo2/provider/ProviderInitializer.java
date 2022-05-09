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

package io.appactive.rpc.apache.dubbo2.provider;

import java.util.HashMap;
import java.util.Map;

import io.appactive.java.api.base.constants.ResourceActiveType;
import io.appactive.java.api.bridge.rpc.constants.constant.RPCConstant;
import io.appactive.java.api.bridge.rpc.provider.RPCProviderInitService;
import io.appactive.java.api.rule.machine.AbstractMachineUnitRuleService;
import io.appactive.java.api.utils.lang.StringUtils;
import io.appactive.rule.ClientRuleService;
import io.appactive.support.lang.CollectionUtils;
import io.appactive.support.log.LogUtil;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.config.ConfigInitializer;
import org.apache.dubbo.config.ServiceConfig;
import org.slf4j.Logger;

import static org.apache.dubbo.common.constants.CommonConstants.PROVIDER;

@Activate(group = PROVIDER, order = 200)
public class ProviderInitializer implements ConfigInitializer, RPCProviderInitService<Map<String, String>> {
    private static final Logger logger = LogUtil.getLogger();

    private final AbstractMachineUnitRuleService machineUnitRuleService = ClientRuleService.getMachineUnitRuleService();


    public ProviderInitializer() {
        logger.info("init-ProviderInitializer");
    }

    @Override
    public void initServiceConfig(ServiceConfig serviceConfig) {
        // 针对特殊的内置 org.apache.dubbo.metadata.MetadataService 处理，无须走后续流程，正常的 parameters 都不会有值
        Map<String, String> parameters = serviceConfig.getParameters();
        if (parameters == null){
            // 针对普通服务的修正
            parameters = new HashMap<>(2);
            parameters.put(RPCConstant.URL_RESOURCE_ACTIVE_LABEL_KEY, ResourceActiveType.NORMAL_RESOURCE_TYPE);
            parameters.put(RPCConstant.URL_UNIT_LABEL_KEY, machineUnitRuleService.getCurrentUnit());
            serviceConfig.setParameters(parameters);
            return;
        }

        // 1. 增加多活相关参数
        addUnitFlagAttribute(parameters);
        addRouteIndexAttribute(parameters);
        addResourceTypeAttribute(parameters);
        // 2. 默认暂不往全局注册，后续控制功能开源后，再加进来
        logger.info("init-refer:{}", parameters.toString());
    }

    @Override
    public void addUnitFlagAttribute( Map<String, String> parameters) {
        addProperty(parameters, RPCConstant.URL_UNIT_LABEL_KEY, machineUnitRuleService.getCurrentUnit());
    }

    @Override
    public void addRouteIndexAttribute( Map<String, String> parameters) {
        addPropertyAndRemoveOldKey(parameters, RPCConstant.URL_ROUTE_INDEX, RPCConstant.URL_ROUTE_INDEX_KEY);
    }

    @Override
    public void addResourceTypeAttribute( Map<String, String> parameters) {
        addPropertyAndRemoveOldKey(parameters, RPCConstant.URL_RESOURCE_ACTIVE_LABEL, RPCConstant.URL_RESOURCE_ACTIVE_LABEL_KEY);
    }


    private void addPropertyAndRemoveOldKey( Map<String, String> parameters, String oldKey,String newKey) {
        if (CollectionUtils.isEmpty(parameters)){
            return;
        }
        String s = parameters.get(oldKey);
        if (StringUtils.isBlank(s)){
            return;
        }
        parameters.put(newKey,s);
        parameters.remove(oldKey);
    }


    private void addProperty(Map<String, String> parameters, String propertyKey, String wantSaveValue) {
        if (CollectionUtils.isEmpty(parameters)){
            return;
        }
        String property = parameters.get(propertyKey);
        if (StringUtils.isNotBlank(property)) {
            // 存在值
            return;
        }
        if (StringUtils.isBlank(wantSaveValue)) {
            // 待存的值为空，则不处理
            return;
        }
        parameters.put(propertyKey, wantSaveValue);
    }

}
