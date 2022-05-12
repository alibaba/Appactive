package io.appactive.springcloud.eureka;

import io.appactive.rpc.springcloud.common.provider.URIRegister;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * @author mageekchiu
 */
@Configuration
public class EurekaAutoConfig {

    @Autowired
    private List<FilterRegistrationBean> beanList;

    @PostConstruct
    public void init(){
        URIRegister.collectUris(beanList);
    }
}
