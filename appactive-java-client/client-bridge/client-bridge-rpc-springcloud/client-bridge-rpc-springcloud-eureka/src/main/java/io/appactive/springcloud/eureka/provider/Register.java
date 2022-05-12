package io.appactive.springcloud.eureka.provider;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.netflix.eureka.serviceregistry.EurekaRegistration;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Map;

public class Register {


    @PostConstruct
    public void doRegister(){
        doRegisterEureka();
    }

    /// another way
    // @Qualifier("eurekaApplicationInfoManager")
    // @Autowired
    // private ApplicationInfoManager aim;
    // public void doRegisterEureka(){
    //     Map<String, String> map = aim.getInfo().getMetadata();
    //     map.put("dynamic-s1", "value_2");
    // }

    @Autowired
    private EurekaRegistration eurekaRegistration;
    public void doRegisterEureka(){
        /// another way
        // Map<String, String> map = eurekaRegistration.getApplicationInfoManager().getInfo().getMetadata();
        Map<String, String> map = eurekaRegistration.getMetadata();
    }

}
