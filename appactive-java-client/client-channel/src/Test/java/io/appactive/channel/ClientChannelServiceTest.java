package io.appactive.channel;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import io.appactive.java.api.channel.ConverterInterface;
import io.appactive.java.api.rule.machine.bo.MachineUnitBO;
import org.junit.Test;

public class ClientChannelServiceTest {

    @Test
    public void getConfigReadDataSource() {
        ConverterInterface<String, MachineUnitBO> ruleConverterInterface = source -> JSON.parseObject(source,
                new TypeReference<MachineUnitBO>() {});

        MachineUnitBO b = ruleConverterInterface.convert("{\"unitFlag\":\"unit\"}");
        System.out.println(b);
    }
}