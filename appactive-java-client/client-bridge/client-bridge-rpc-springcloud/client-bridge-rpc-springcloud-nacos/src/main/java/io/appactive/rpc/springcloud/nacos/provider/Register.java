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
@Component
public class Register {

    private final AbstractMachineUnitRuleService machineUnitRuleService = ClientRuleService.getMachineUnitRuleService();

    private URIRegister uriRegister;

    @Autowired(required = false)
    public void setUriRegister(URIRegister uriRegister) {
        this.uriRegister = uriRegister;
    }

    @PostConstruct
    public void doRegister(){
        doRegisterNacos();
    }

    @Autowired(required = false)
    private NacosRegistration nacosRegistration;
    public void doRegisterNacos(){
        Map<String, String> map = nacosRegistration.getMetadata();
        map.put(RPCConstant.URL_UNIT_LABEL_KEY, machineUnitRuleService.getCurrentUnit());
        if (uriRegister==null){
            return;
        }
        ServiceMetaObject serviceMetaObject = uriRegister.getServiceMetaObject();
        if(serviceMetaObject == null){
            return;
        }
        map.put(RPCConstant.SPRING_CLOUD_SERVICE_META, serviceMetaObject.getMeta());
        map.put(RPCConstant.SPRING_CLOUD_SERVICE_META_VERSION, serviceMetaObject.getMd5());
    }



}
