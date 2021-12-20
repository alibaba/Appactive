package io.appactive.java.api.rule.machine;

import io.appactive.java.api.base.extension.SPI;
import io.appactive.java.api.rule.machine.bo.MachineUnitBO;

@SPI
public abstract class AbstractMachineUnitRuleService {

    /**
     * machine unit flag
     * @return
     */
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

    /**
     * 获得当前机器所在的单元标识
     *
     * @return
     */
    public String getCurrentUnit(){
        MachineUnitBO machineUnitBO = getMachineUnitBO();
        if (machineUnitBO == null) {
            return null;
        }
        return machineUnitBO.getUnitFlag();
    }

}
