package io.appactive.rpc.springcloud.common.consumer;

/**
 * @author mageekchiu
 */

import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.loadbalancer.core.DelegatingServiceInstanceListSupplier;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import org.springframework.context.ConfigurableApplicationContext;
import reactor.core.publisher.Flux;

import java.util.List;

/**
 *
 */
public class ServerListFilterSupplier extends DelegatingServiceInstanceListSupplier {

    private static final ConsumerRouter<ServiceInstance> CONSUMER_ROUTER = new ConsumerRouter<>(ServiceInstance.class);

    public ServerListFilterSupplier(ServiceInstanceListSupplier delegate, ConfigurableApplicationContext context) {
        super(delegate);
    }

    @Override
    public Flux<List<ServiceInstance>> get() {
        return delegate.get().map(this::filtering);
    }

    private List<ServiceInstance> filtering(List<ServiceInstance> serviceInstances) {
        // instant calculating without caching
        List<ServiceInstance> filteredInstances = CONSUMER_ROUTER.filterWithoutCache(serviceInstances);
        return filteredInstances;
    }

}
