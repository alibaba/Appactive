package io.appactive.java.api.bridge.rpc.provider;

import org.apache.dubbo.rpc.Result;

/**
 *
 */
public interface RPCProviderProtectService<T,P,meta> {

    /**
     * check normal service
     *
     * @return null: do nothing result: error
     */
    Result normalResourceProtect(T invocation, meta serviceMetadata);

    /**
     * check unit service
     *
     * @return null: do nothing result: error
     */
    Result unitResourceProtect(T invocation, P providerModel);

    /**
     * check center service
     *
     * @return null: do nothing result: error
     */
    Result centerResourceProtect(T invocation,meta serviceMetadata);
}
