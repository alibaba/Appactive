package io.appactive.rpc.springcloud.provider;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class Register {

    @Autowired
    private DiscoveryClient discoveryClient;

    @Value("${spring.application.name}")
    private String appName;

    @PostConstruct
    public void doRegister(){
        for (ServiceInstance storage : discoveryClient.getInstances(appName)) {
            System.out.println("doRegister "+storage.getMetadata());
        }

    }

}
