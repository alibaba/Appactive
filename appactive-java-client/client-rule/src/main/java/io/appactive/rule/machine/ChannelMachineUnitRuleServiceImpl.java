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

package io.appactive.rule.machine;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import io.appactive.channel.ClientChannelService;
import io.appactive.java.api.channel.ConfigReadDataSource;
import io.appactive.java.api.channel.ConverterInterface;
import io.appactive.java.api.channel.listener.DataListener;
import io.appactive.java.api.rule.RuleTypeEnum;
import io.appactive.java.api.rule.machine.AbstractMachineUnitRuleService;
import io.appactive.java.api.rule.machine.bo.MachineUnitBO;
import io.appactive.rule.ClientRuleService;

public class ChannelMachineUnitRuleServiceImpl extends AbstractMachineUnitRuleService {

    private MachineUnitBO machineUnitBO;

    private final DataListener<MachineUnitBO> listener = new DataListener<MachineUnitBO>() {
        @Override
        public String getListenerName() {
            return "MachineUnitBO-Listener-"+this.hashCode();
        }

        @Override
        public void dataChanged(MachineUnitBO oldData, MachineUnitBO newData) {
            machineUnitBO = newData;
        }
    };

    public ChannelMachineUnitRuleServiceImpl() {
        initFromUri(ClientRuleService.getDefaultUri(RuleTypeEnum.machineRule));
    }

    public ChannelMachineUnitRuleServiceImpl(String uri) {
        initFromUri(uri);
    }

    private void initFromUri(String uri) {
        ConverterInterface<String, MachineUnitBO> ruleConverterInterface = (source) -> JSON.parseObject(source,new TypeReference<MachineUnitBO>() {});
        ConfigReadDataSource<MachineUnitBO> fileReadDataSource = ClientChannelService.getConfigReadDataSource(uri,ruleConverterInterface);
        /// error
        // ConfigReadDataSource<MachineUnitBO> fileReadDataSource = ClientChannelService.getConfigReadDataSource(uri);
        fileReadDataSource.addDataChangedListener(listener);
    }


    @Override
    protected MachineUnitBO getMachineUnitBO() {
        return machineUnitBO;
    }
}
