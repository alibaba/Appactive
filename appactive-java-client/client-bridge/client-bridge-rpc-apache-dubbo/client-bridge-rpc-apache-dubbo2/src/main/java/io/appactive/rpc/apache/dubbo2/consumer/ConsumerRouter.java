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

package io.appactive.rpc.apache.dubbo2.consumer;

import java.text.MessageFormat;
import java.util.List;

import io.appactive.java.api.base.enums.MiddleWareTypeEnum;
import io.appactive.java.api.base.exception.ExceptionFactory;
import io.appactive.java.api.bridge.rpc.constants.constant.RPCConstant;
import io.appactive.java.api.bridge.rpc.consumer.RPCAddressFilterByUnitService;
import io.appactive.java.api.base.AppContextClient;
import io.appactive.java.api.rule.TrafficMachineService;
import io.appactive.java.api.rule.machine.AbstractMachineUnitRuleService;
import io.appactive.java.api.rule.traffic.TrafficRouteRuleService;
import io.appactive.rpc.apache.dubbo2.consumer.callback.Dubbo2AddressCallBack;
import io.appactive.rpc.base.consumer.RPCAddressFilterByUnitServiceImpl;
import io.appactive.rule.ClientRuleService;
import io.appactive.support.lang.CollectionUtils;
import io.appactive.support.log.LogUtil;
import org.apache.dubbo.common.URL;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.RpcContext;
import org.apache.dubbo.rpc.RpcException;
import org.apache.dubbo.rpc.cluster.Router;
import org.apache.dubbo.rpc.model.ApplicationModel;
import org.apache.dubbo.rpc.model.ConsumerModel;
import org.slf4j.Logger;

public class ConsumerRouter implements Router {

    private static final Logger logger = LogUtil.getLogger();

    private static final int CONSUMER_ROUTER_DEFAULT_PRIORITY = -10;
    private boolean force = false;
    private URL url;

    private final TrafficRouteRuleService trafficRouteRuleService = ClientRuleService.getTrafficRouteRuleService();
    private final AbstractMachineUnitRuleService machineUnitRuleService = ClientRuleService.getMachineUnitRuleService();
    private final TrafficMachineService trafficMachineService = new TrafficMachineService(trafficRouteRuleService,
        machineUnitRuleService);

    private RPCAddressFilterByUnitService addressFilterByUnitService = null;

    private Dubbo2AddressCallBack callBack;

    private ConsumerModel consumerModel;

    private String servicePrimaryKey;

    private Integer routeIdIndex = null;

    public ConsumerRouter(URL referenceUrl) {
        this.url = referenceUrl;
        this.servicePrimaryKey = referenceUrl.getServiceKey();
        this.consumerModel = ApplicationModel.getConsumerModel(servicePrimaryKey);
        this.callBack = new Dubbo2AddressCallBack();
        logger.info("init-ConsumerUnitRouter");
    }

    //-------------------------------------------------------------------------------------------

    @Override
    public URL getUrl() {
        return url;
    }

    @Override
    public <T> List<Invoker<T>> route(List<Invoker<T>> invokers, URL url, Invocation invocation) throws RpcException {
        String indexValue = null;
        if (this.routeIdIndex != null && this.routeIdIndex >= 0){
            // 获得显示透传的route
            indexValue = getRouteIndexValue(invocation);
        }else {
            // 隐式透传
            RpcContext.getContext().setAttachment(RPCConstant.CONSUMER_REMOTE_ROUTE_ID_KEY, AppContextClient.getRouteId());
        }

        List<Invoker<T>> list = addressFilterByUnitService.addressFilter(null, servicePrimaryKey,indexValue);
        return list;
    }

    @Override
    public boolean isRuntime() {
        return true;
    }

    @Override
    public boolean isForce() {
        return force;
    }

    @Override
    public int getPriority() {
        return CONSUMER_ROUTER_DEFAULT_PRIORITY;
    }

    @Override
    public <T> void notify(List<Invoker<T>> invokers) {
        initRouteIdIndex(invokers);

        if (!trafficRouteRuleService.haveTrafficRule()) {
            // 非单元化，不处理
            return;
        }
        if (addressFilterByUnitService == null) {
            addressFilterByUnitService = new RPCAddressFilterByUnitServiceImpl<T>(MiddleWareTypeEnum.DUBBO);
            addressFilterByUnitService.initAddressCallBack(callBack);
        }
        addressFilterByUnitService.refreshAddressList(null, servicePrimaryKey, invokers);
    }

    private <T> void initRouteIdIndex(List<Invoker<T>> invokers) {
        if (this.routeIdIndex != null){
            return;
        }
        if (CollectionUtils.isEmpty(invokers)){
            return;
        }
        for (Invoker<T> invoker : invokers) {
            String metaMapValue = callBack.getMetaMapValue(invoker, RPCConstant.URL_ROUTE_INDEX_KEY);
            if (metaMapValue != null){
                this.routeIdIndex = Integer.parseInt(metaMapValue);
                return;
            }
        }
        this.routeIdIndex = -1;
    }

    private String getRouteIndexValue(Invocation invocation) {
        try {
            String routeId = String.valueOf(invocation.getArguments()[routeIdIndex]);
            return routeId;
        } catch (Throwable throwable) {
            String msg = MessageFormat.format("service:{0}, error when get routeId in params, routeIdIndex:{1}",
                servicePrimaryKey, routeIdIndex);
            throw ExceptionFactory.makeFault(msg, throwable);
        }
    }

}
