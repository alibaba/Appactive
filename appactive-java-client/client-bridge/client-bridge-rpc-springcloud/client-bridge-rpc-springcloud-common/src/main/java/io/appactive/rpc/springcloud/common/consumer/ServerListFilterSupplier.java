package io.appactive.rpc.springcloud.common.consumer;

import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.loadbalancer.core.DelegatingServiceInstanceListSupplier;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class ServerListFilterSupplier extends DelegatingServiceInstanceListSupplier {

    public ServerListFilterSupplier(ServiceInstanceListSupplier delegate) {
        super(delegate);
    }

    @Override
    public Flux<List<ServiceInstance>> get() {
        return delegate.get().map(this::filtering);
    }

    private List<ServiceInstance> filtering(List<ServiceInstance> serviceInstances) {
        // instant calculating without caching
        List<ServiceInstance> filteredInstances = new ArrayList<>();
        return serviceInstances;
    }

}
