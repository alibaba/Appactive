package io.appactive.rule.machine;

import io.appactive.java.api.rule.machine.AbstractMachineUnitRuleService;
import io.appactive.java.api.rule.machine.bo.MachineUnitBO;
import io.appactive.support.sys.JvmPropertyUtil;
import io.appactive.rule.base.property.RulePropertyConstant;

public class JVMMachineUnitRuleServiceImpl extends AbstractMachineUnitRuleService {

    @Override
    protected MachineUnitBO getMachineUnitBO() {
        String jvmLocalUnit = JvmPropertyUtil.getJvmAndEnvValue(RulePropertyConstant.UNIT_LAG_PROPERTY_KEY);
        return new MachineUnitBO(jvmLocalUnit);
    }
}
