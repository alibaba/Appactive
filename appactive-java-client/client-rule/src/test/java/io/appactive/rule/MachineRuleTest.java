package io.appactive.rule;

import io.appactive.BaseTest;
import io.appactive.java.api.rule.machine.AbstractMachineUnitRuleService;
import io.appactive.rule.machine.FileMachineUnitRuleServiceImpl;
import org.junit.Assert;
import org.junit.Test;

public class MachineRuleTest extends BaseTest {



    @Test
    public void testRule(){
        String path = TEST_RESOURCE_PATH+"machine/machine.json";

        AbstractMachineUnitRuleService service = new FileMachineUnitRuleServiceImpl(path);

        String currentUnit = service.getCurrentUnit();
        boolean centerUnit = service.isCenterUnit();

        Assert.assertEquals(currentUnit,"A");
        Assert.assertFalse(centerUnit);
    }
}
