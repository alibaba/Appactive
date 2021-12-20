package io.appactive.java.api.bridge.rpc.consumer;

import java.util.List;

import io.appactive.java.api.bridge.rpc.constants.bo.RPCInvokerBO;

public interface RPCAddressCallBack<T> {

    /**
     * 获得当前 server 的 标签map
     * @param server rpc 框架的server 对象，要查询的 key
     * @return value
     */
    String getMetaMapValue(T server,String key);

    /**
     * server 的 string 内容，用于打印和唯一使用
     */
    String getServerToString(T server);

    /**
     * 原始地址变更为 优先需要的统一地址
     */
    List<RPCInvokerBO<T>> changeToRPCInvokerBOList(List<T> servers);

    /**
     * 优先地址更改回原始地址
     */
    List<T> changedToOriginalInvokerList(List<RPCInvokerBO<T>> RPCInvokerBOS);
}
