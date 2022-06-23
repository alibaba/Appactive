package io.appactive.rpc.springcloud.nacos.consumer;

import com.alibaba.cloud.nacos.ribbon.NacosServer;
import com.netflix.loadbalancer.Server;
import io.appactive.java.api.base.exception.ExceptionFactory;
import io.appactive.rpc.springcloud.common.consumer.ServerMetaService;
import org.springframework.cloud.client.ServiceInstance;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * @author mageekchiu
 */
public class NacosServerMeta<T> implements ServerMetaService<T> {

    private static final Map<String, String> EMPTY_MAP = new HashMap<>();
    private static final String EMPTY_APP_NAME = "EMPTY_APP_NAME";
    private static final String EMPTY_INSTANCE_NAME = "EMPTY_INSTANCE_NAME";

    private Function<T,Map<String,String>> getMetaMap ;
    private Function<T,String> getAppName ;
    private Function<T,String> getInstanceName ;

    private Class<T> type;

    public NacosServerMeta(Class<T> type) {
        this.type = type;
        if (type == Server.class){
            getMetaMap = (server)->{
                return ((NacosServer)server).getMetadata();
            };
            getAppName = (server)->{
                return ((NacosServer)server).getMetaInfo().getAppName();
            };
            getInstanceName = (server)->{
                return ((NacosServer)server).getHostPort();
            };
            return;
        }
        if (type == ServiceInstance.class){
            getMetaMap = (server)->{
                return ((ServiceInstance)server).getMetadata();
            };
            getAppName = (server)->{
                return ((ServiceInstance)server).getServiceId();
            };
            getInstanceName = (server)->{
                ServiceInstance s = ((ServiceInstance)server);
                return s.getHost()+":"+s.getPort();
            };
            return;
        }
        throw ExceptionFactory.makeFault("unsupported class");
    }

    @Override
    public Map<String, String> getMetaMap(T server) {
        return getMetaMap.apply(server);
    }

    @Override
    public String getAppName(T server) {
        return getAppName.apply(server);
    }

    @Override
    public String getInstanceName(T server) {
        return getInstanceName.apply(server);
    }
}
