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

package io.appactive.java.api.rule.machine;

import io.appactive.java.api.base.extension.SPI;
import io.appactive.java.api.rule.machine.bo.MachineUnitBO;

@SPI
public abstract class AbstractMachineUnitRuleService {


    protected abstract MachineUnitBO getMachineUnitBO();

    /**
     * 是否为中心单元 若当前机器所在单元为 null，属于非法名称，默认为 false，注意：在底层会做非单元化的默认中心的行为，理论上均会有值
     *
     * @return true：是中心单元
     */
    public boolean isCenterUnit() {
        MachineUnitBO machineUnitBO = getMachineUnitBO();
        if (machineUnitBO == null) {
            return true;
        }
        return machineUnitBO.getCheckIsCenterFlag();
    }

    public String getCurrentUnit(){
        MachineUnitBO machineUnitBO = getMachineUnitBO();
        if (machineUnitBO == null) {
            return null;
        }
        return machineUnitBO.getUnitFlag();
    }

}
