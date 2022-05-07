package io.appactive.rpc.springcloud.nacos.consumer;

import com.netflix.loadbalancer.Server;
import io.appactive.rpc.springcloud.common.consumer.ServerMeta;
import org.springframework.cloud.alibaba.nacos.ribbon.NacosServer;

import java.util.Map;

/**
 * @author mageekchiu
 */
public class NacosServerMeta implements ServerMeta {

    public NacosServerMeta() {
    }

    @Override
    public Map<String, String> getMetaMap(Server server) {
        return ((NacosServer)server).getMetadata();
    }
}
