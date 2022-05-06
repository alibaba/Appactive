package io.appactive.rpc.springcloud.common.consumer;

import com.netflix.loadbalancer.Server;

import java.util.Map;

/**
 * @author mageekchiu
 */
public interface ServerMeta {

    Map<String, String> getMetaMap(Server server);
}
