package io.appactive.demo.frontend.config;

import io.appactive.rpc.springcloud.common.consumer.ServerListFilterSupplier;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

/**
 * @author mageekchiu
 */
public class LBConfig {
    // https://github.com/spring-cloud/spring-cloud-commons/pull/989
    // spring cloud customize ServiceInstanceListSupplier
    // https://github.com/spring-cloud/spring-cloud-commons/issues/922
    @Bean
    public ServiceInstanceListSupplier discoveryClientServiceInstanceListSupplier(ConfigurableApplicationContext context) {
        return ServiceInstanceListSupplier.builder()
                .withBlockingDiscoveryClient()
                .with((context1, delegate) -> new ServerListFilterSupplier(delegate, context1))
                .build(context);
    }
}
