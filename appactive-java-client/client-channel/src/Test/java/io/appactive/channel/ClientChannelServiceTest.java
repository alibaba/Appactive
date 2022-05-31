package io.appactive.channel;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.PropertyKeyConst;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.exception.NacosException;
import io.appactive.java.api.channel.ConverterInterface;
import io.appactive.java.api.rule.machine.bo.MachineUnitBO;
import org.junit.Test;

import java.util.Properties;

public class ClientChannelServiceTest {

    @Test
    public void getConfigReadDataSource() {
        ConverterInterface<String, MachineUnitBO> ruleConverterInterface = source -> JSON.parseObject(source,
                new TypeReference<MachineUnitBO>() {});

        MachineUnitBO b = ruleConverterInterface.convert("{\"unitFlag\":\"unit\"}");
        System.out.println(b);
    }

    @Test
    public void getConfig() {
        try {
            String dataId = "appactive.dataId.idSourceRulePath";
            String group = "appactive.groupId";
            Properties properties = new Properties();
            properties.put(PropertyKeyConst.SERVER_ADDR, "127.0.0.1:8848");
            properties.put(PropertyKeyConst.NAMESPACE, "appactiveDemoNamespaceId");
            ConfigService configService = NacosFactory.createConfigService(properties);
            String content = configService.getConfig(dataId, group, 5000);
            System.out.println("content:"+content);
        } catch (NacosException e) {
            e.printStackTrace();
        }
    }
}