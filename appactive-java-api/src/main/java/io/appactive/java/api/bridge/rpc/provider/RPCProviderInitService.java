package io.appactive.java.api.bridge.rpc.provider;


public interface RPCProviderInitService<T> {

    void addUnitFlagAttribute(T t);

    void addRouteIndexAttribute(T t);

    void addResourceTypeAttribute(T t);
}
