package io.appactive.java.api.bridge.rpc.consumer;

import java.util.List;


public interface RPCAddressFilterByUnitService<T> {

    void initAddressCallBack(RPCAddressCallBack<T> callBack);

    /**
     * init service list
     * @param servicePrimaryName primaryKey, dubbo: service+group+verison
     * @param list service remote ip list
     */
    void refreshAddressList(String providerAppName,String servicePrimaryName,List<T> list);

    /**
     * used with refreshAddressList， filter address
     */
    List<T> addressFilter(String providerAppName, String servicePrimaryName,String routeId);


    /**
     * 路由选址核心逻辑
     * @param providerAppName appName
     * @param servicePrimaryName 同机房优先的service 唯一标示，一般 springcloud 为 app+uri，hsf/dubbo 为 service+group+version
     * @param list 服务提供的列表
     *
     */
    List<T> addressFilter(String providerAppName, String servicePrimaryName, List<T> list,String routeId);

}
