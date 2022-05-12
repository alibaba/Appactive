package io.appactive.rpc.springcloud.nacos.provider;


import io.appactive.java.api.bridge.rpc.constants.constant.RPCConstant;
import io.appactive.java.api.rule.machine.AbstractMachineUnitRuleService;
import io.appactive.rpc.springcloud.common.ServiceMetaObject;
import io.appactive.rpc.springcloud.common.provider.URIRegister;
import io.appactive.rule.ClientRuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.alibaba.nacos.registry.NacosRegistration;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Map;

/**
 * @author mageekchiu
 */
public class Register {

    private static final AbstractMachineUnitRuleService machineUnitRuleService = ClientRuleService.getMachineUnitRuleService();

    public static void doRegisterNacos(NacosRegistration nacosRegistration){
        Map<String, String> map = nacosRegistration.getMetadata();
        map.put(RPCConstant.URL_UNIT_LABEL_KEY, machineUnitRuleService.getCurrentUnit());
        ServiceMetaObject serviceMetaObject = URIRegister.getServiceMetaObject();
        if(serviceMetaObject == null){
            return;
        }
        map.put(RPCConstant.SPRING_CLOUD_SERVICE_META, serviceMetaObject.getMeta());
        map.put(RPCConstant.SPRING_CLOUD_SERVICE_META_VERSION, serviceMetaObject.getMd5OfList());
    }



}
