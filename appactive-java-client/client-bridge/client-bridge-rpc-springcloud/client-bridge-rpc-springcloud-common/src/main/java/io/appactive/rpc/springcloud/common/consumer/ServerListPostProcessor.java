package io.appactive.rpc.springcloud.common.consumer;

import io.appactive.support.log.LogUtil;
import org.slf4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;

/**
 * @author mageekchiu
 */
@Configuration
public class ServerListPostProcessor implements BeanPostProcessor {

    private static final Logger logger = LogUtil.getLogger();

    @Autowired
    ApplicationContext context;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof ServiceInstanceListSupplier){
            logger.info("ServerListPostProcessor replacing defaultSupplier {} ......",beanName);
            ServiceInstanceListSupplier supplier = (ServiceInstanceListSupplier) bean;
            return new ServerListFilterSupplier(supplier);
        }
        return bean;
    }
}
