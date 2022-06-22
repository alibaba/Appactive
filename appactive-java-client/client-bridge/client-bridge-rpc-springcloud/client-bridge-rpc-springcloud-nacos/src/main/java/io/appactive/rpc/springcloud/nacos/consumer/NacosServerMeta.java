package io.appactive.rpc.springcloud.nacos.consumer;

import com.alibaba.cloud.nacos.ribbon.NacosServer;
import com.netflix.loadbalancer.Server;
import io.appactive.rpc.springcloud.common.consumer.ServerMetaService;
import org.springframework.cloud.client.ServiceInstance;

import java.util.HashMap;
import java.util.Map;

/**
 * @author mageekchiu
 */
public class NacosServerMeta<T> implements ServerMetaService<T> {

    private static final Map<String, String> EMPTY_MAP = new HashMap<>();
    private static final String EMPTY_APP_NAME = "EMPTY_APP_NAME";
    private static final String EMPTY_INSTANCE_NAME = "EMPTY_INSTANCE_NAME";

    public NacosServerMeta() {
    }

    @Override
    public Map<String, String> getMetaMap(T server) {
        if (server instanceof  Server){
            return ((NacosServer)server).getMetadata();
        }
        if (server instanceof ServiceInstance){
            return ((ServiceInstance)server).getMetadata();
        }
        return EMPTY_MAP;
    }

    @Override
    public String getAppName(T server) {
        if (server instanceof  Server){
            return ((NacosServer)server).getMetaInfo().getAppName();
        }
        if (server instanceof ServiceInstance){
            return ((ServiceInstance)server).getScheme();
        }
        return EMPTY_APP_NAME;
    }

    @Override
    public String getInstanceName(T server) {
        if (server instanceof  Server){
            return ((NacosServer)server).getHostPort();
        }
        if (server instanceof ServiceInstance){
            ServiceInstance s = ((ServiceInstance)server);
            return s.getHost()+":"+s.getPort();
        }
        return EMPTY_INSTANCE_NAME;
    }
}
