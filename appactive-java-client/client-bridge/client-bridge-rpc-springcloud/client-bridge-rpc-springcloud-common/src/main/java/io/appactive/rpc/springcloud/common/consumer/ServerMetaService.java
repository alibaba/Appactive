package io.appactive.rpc.springcloud.common.consumer;

import java.util.Map;

/**
 * @author mageekchiu
 */
public interface ServerMetaService<T> {

    Map<String, String> getMetaMap(T server);
    String getAppName(T server);
    String getInstanceName(T server);
}
