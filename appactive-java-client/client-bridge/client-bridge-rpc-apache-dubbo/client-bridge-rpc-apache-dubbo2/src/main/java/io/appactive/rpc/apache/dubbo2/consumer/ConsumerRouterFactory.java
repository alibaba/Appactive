package io.appactive.rpc.apache.dubbo2.consumer;

import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.cluster.Router;
import org.apache.dubbo.rpc.cluster.RouterFactory;

@Activate(order = 200)
public class ConsumerRouterFactory implements RouterFactory {
    @Override
    public Router getRouter(URL url) {
        return new ConsumerRouter(url);
    }
}
