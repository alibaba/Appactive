package io.appactive.springcloud.eureka.consumer;

import com.netflix.loadbalancer.Server;
import com.netflix.niws.loadbalancer.DiscoveryEnabledServer;
import io.appactive.rpc.springcloud.common.consumer.ServerMeta;

import java.util.Map;

/**
 * @author mageekchiu
 */
public class EurekaServerMeta implements ServerMeta {

    public EurekaServerMeta() {
    }

    @Override
    public Map<String, String> getMetaMap(Server server) {
        return ((DiscoveryEnabledServer)server).getInstanceInfo().getMetadata();
    }
}
