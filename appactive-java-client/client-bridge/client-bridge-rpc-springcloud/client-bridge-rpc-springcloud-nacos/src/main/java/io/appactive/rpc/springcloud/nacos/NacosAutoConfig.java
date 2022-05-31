package io.appactive.rpc.springcloud.nacos;

import com.alibaba.cloud.nacos.registry.NacosRegistration;
import io.appactive.rpc.springcloud.common.provider.URIRegister;
import io.appactive.rpc.springcloud.nacos.provider.Register;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * @author mageekchiu
 */
@Configuration
public class NacosAutoConfig {

    @Autowired
    private List<FilterRegistrationBean> beanList;

    @Autowired(required = false)
    private NacosRegistration nacosRegistration;


    @PostConstruct
    public void init(){
        URIRegister.collectUris(beanList);
        Register.doRegisterNacos(nacosRegistration);
    }
}
