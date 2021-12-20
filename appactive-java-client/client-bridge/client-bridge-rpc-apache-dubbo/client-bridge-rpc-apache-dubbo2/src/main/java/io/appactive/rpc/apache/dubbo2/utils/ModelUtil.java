package io.appactive.rpc.apache.dubbo2.utils;

import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.model.ApplicationModel;
import org.apache.dubbo.rpc.model.ProviderModel;

public class ModelUtil {
    public ModelUtil() {
    }

    public static ProviderModel getProviderModel(Invocation invocation) {
        Object o = invocation.get("providerModel");
        if (o instanceof ProviderModel) {
            return (ProviderModel)o;
        } else {
            String serviceKey = invocation.getInvoker().getUrl().getServiceKey();
            return ApplicationModel.getProviderModel(serviceKey);
            //return FrameworkModel.defaultModel().getServiceRepository().lookupExportedService(serviceKey);
        }
    }


    private static String stripGroupIfNecessary(String serviceName) {
        int index = serviceName.indexOf(47);
        return index > 0 ? serviceName.substring(index + 1) : serviceName;
    }
}
