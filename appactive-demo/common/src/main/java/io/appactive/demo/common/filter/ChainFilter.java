package io.appactive.demo.common.filter;

import io.appactive.demo.common.entity.ResultHolder;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.*;

@Activate
public class ChainFilter implements Filter {
    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        Result result = invoker.invoke(invocation);
        Object object = result.getValue();
        System.out.println(object);
        if (object instanceof ResultHolder){
            ResultHolder resultHolder = (ResultHolder)object;
            resultHolder.addChain(System.getenv("io.appactive.demo.app"),System.getenv("io.appactive.demo.unit"));
            result.setValue(resultHolder);
        }
        return result;
    }
}
