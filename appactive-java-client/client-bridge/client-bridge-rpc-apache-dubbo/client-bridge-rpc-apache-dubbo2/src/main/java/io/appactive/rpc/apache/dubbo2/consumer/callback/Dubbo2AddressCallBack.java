package io.appactive.rpc.apache.dubbo2.consumer.callback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.appactive.java.api.bridge.rpc.constants.bo.RPCInvokerBO;
import io.appactive.java.api.bridge.rpc.consumer.RPCAddressCallBack;
import org.apache.dubbo.common.URL;
import org.apache.dubbo.rpc.Invoker;

public class Dubbo2AddressCallBack<T> implements RPCAddressCallBack<Invoker<T>> {

    @Override
    public String getMetaMapValue(Invoker<T> server, String key) {
        if (server == null){
            return null;
        }
        URL url = server.getUrl();
        if (url == null){
            return null;
        }
        return url.getParameter(key);
    }

    @Override
    public String getServerToString(Invoker<T> server) {
        if (server == null){
            return null;
        }
        URL url = server.getUrl();
        if (url == null){
            return null;
        }
        return url.getAddress();
    }

    @Override
    public List<RPCInvokerBO<Invoker<T>>> changeToRPCInvokerBOList(List<Invoker<T>> servers) {
        return toRPCInvokerBOList(servers);
    }

    @Override
    public List<Invoker<T>> changedToOriginalInvokerList(List<RPCInvokerBO<Invoker<T>>> RPCInvokerBOS) {
        return toOriginalInvokerList(RPCInvokerBOS);
    }


    private List<RPCInvokerBO<Invoker<T>>> toRPCInvokerBOList(List<Invoker<T>> invokers) {

        List<RPCInvokerBO<Invoker<T>>> invokerBOS = new ArrayList<>();

        for(Invoker<T> invoker:invokers){
            RPCInvokerBO<Invoker<T>> rpcInvokerBO = new RPCInvokerBO<Invoker<T>>();
            rpcInvokerBO.setOriginalValue(invoker);

            Map<String, String> parameters = invoker.getUrl().getParameters();
            String host = invoker.getUrl().getHost();
            int port = invoker.getUrl().getPort();
            Map<String, String> defaultMetaMap = new HashMap<>(parameters);
            defaultMetaMap.put("host",String.valueOf(host));
            defaultMetaMap.put("port",String.valueOf(port));
            rpcInvokerBO.setDefaultMetaMap(defaultMetaMap);
            invokerBOS.add(rpcInvokerBO);
        }
        return invokerBOS;
    }

    private List<Invoker<T>> toOriginalInvokerList(List<RPCInvokerBO<Invoker<T>>> rpcInvokerBOS) {
        ArrayList<Invoker<T>> invokers = new ArrayList();
        if (rpcInvokerBOS == null){
            return invokers;
        }
        for (RPCInvokerBO<Invoker<T>> rpcInvokerBO : rpcInvokerBOS) {
            invokers.add(rpcInvokerBO.getOriginalValue());
        }
        return invokers;
    }
}
