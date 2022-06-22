package io.appactive.springcloud.eureka.consumer;

import com.netflix.loadbalancer.Server;
import com.netflix.niws.loadbalancer.DiscoveryEnabledServer;
import io.appactive.rpc.springcloud.common.consumer.ServerMetaService;

import java.util.Map;

/**
 * @author mageekchiu
 */
public class EurekaServerMeta<T> implements ServerMetaService<T> {

    public EurekaServerMeta() {
    }

    @Override
    public Map<String, String> getMetaMap(T server) {
        return ((DiscoveryEnabledServer)server).getInstanceInfo().getMetadata();
    }

    @Override
    public String getAppName(T server) {
        return "";
    }

    @Override
    public String getInstanceName(T server) {
        return "";
    }
}
