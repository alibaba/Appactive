/*
 * Copyright 1999-2022 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.appactive.demo.common.filter;

import io.appactive.demo.common.entity.ResultHolder;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.*;

@Activate
public class ChainFilter implements Filter {
    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        Result result = invoker.invoke(invocation);
        // Object object = result.getValue();
        // System.out.println(object);
        // if (object instanceof ResultHolder){
        //     ResultHolder resultHolder = (ResultHolder)object;
        //     resultHolder.addChain(System.getenv("appactive.app"),System.getenv("appactive.unit"));
        //     result.setValue(resultHolder);
        //     System.out.println("ChainFilter: "+resultHolder);
        // }
        return result;
    }
}
