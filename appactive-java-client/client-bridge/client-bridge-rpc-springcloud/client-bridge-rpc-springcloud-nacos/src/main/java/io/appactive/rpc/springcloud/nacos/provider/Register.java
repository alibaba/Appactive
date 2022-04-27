package io.appactive.rpc.springcloud.nacos.provider;


import io.appactive.java.api.bridge.rpc.constants.constant.RPCConstant;
import io.appactive.java.api.rule.machine.AbstractMachineUnitRuleService;
import io.appactive.rule.ClientRuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.alibaba.nacos.registry.NacosRegistration;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Map;

@Component
public class Register {

    private final AbstractMachineUnitRuleService machineUnitRuleService = ClientRuleService.getMachineUnitRuleService();

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private DiscoveryClient discoveryClient;

    @Value("${spring.application.name}")
    private String appName;

    @PostConstruct
    public void doRegister(){
        doRegisterNacos();
    }

    @Autowired(required = false)
    private NacosRegistration nacosRegistration;
    public void doRegisterNacos(){
        /// another way
        // Map<String, String> map = nacosRegistration.getNacosDiscoveryProperties().getMetadata();
        Map<String, String> map = nacosRegistration.getMetadata();
        map.put(RPCConstant.URL_UNIT_LABEL_KEY, machineUnitRuleService.getCurrentUnit());
    }



}
