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

package io.appactive.rpc.base.consumer;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.appactive.java.api.base.exception.AppactiveException;
import io.appactive.java.api.bridge.rpc.constants.constant.RPCConstant;
import io.appactive.java.api.bridge.rpc.consumer.RPCAddressCallBack;
import io.appactive.java.api.bridge.rpc.consumer.RPCAddressFilterByUnitService;
import io.appactive.java.api.base.constants.AppactiveConstant;
import io.appactive.java.api.base.enums.MiddleWareTypeEnum;
import io.appactive.java.api.base.AppContextClient;
import io.appactive.java.api.base.constants.ResourceActiveType;
import io.appactive.java.api.rule.machine.AbstractMachineUnitRuleService;
import io.appactive.java.api.rule.traffic.TrafficRouteRuleService;
import io.appactive.java.api.utils.lang.StringUtils;
import io.appactive.rpc.base.consumer.bo.AddressActive;
import io.appactive.rule.ClientRuleService;
import io.appactive.support.lang.CollectionUtils;
import io.appactive.support.log.LogUtil;
import org.slf4j.Logger;

/**
 */
public class RPCAddressFilterByUnitServiceImpl<T> implements RPCAddressFilterByUnitService<T> {

    private static final Logger logger = LogUtil.getLogger();

    private final MiddleWareTypeEnum middleWareTypeEnum;

    private RPCAddressCallBack<T> rpcUnitCellCallBack;

    private static final String NO_UNIT_LABEL_PROVIDER_TAG_NAME = "NO_UNIT_FLAG_LABEL";

    private final Map<String, AddressActive<T>> SERVICE_REMOTE_ADDRESS_MAP = new ConcurrentHashMap<>();

    private final TrafficRouteRuleService trafficRouteRuleService = ClientRuleService.getTrafficRouteRuleService();

    private final AbstractMachineUnitRuleService machineUnitRuleService = ClientRuleService.getMachineUnitRuleService();

    public RPCAddressFilterByUnitServiceImpl(MiddleWareTypeEnum middleWareTypeEnum) {
        this.middleWareTypeEnum = middleWareTypeEnum;
    }

    @Override
    public void initAddressCallBack(RPCAddressCallBack<T> callBack) {
        this.rpcUnitCellCallBack = callBack;
    }

    @Override
    public void refreshAddressList(String providerAppName,String servicePrimaryName, List<T> list) {
        if (CollectionUtils.isEmpty(list)){
            SERVICE_REMOTE_ADDRESS_MAP.remove(servicePrimaryName);
        }
        String resourceType = getResourceType(servicePrimaryName,list);
        Map<String, List<T>> unitServersMap = transToUnitFlagServerListMap(list);

        AddressActive<T> addressActive = new AddressActive<T>(resourceType,unitServersMap,list);

        SERVICE_REMOTE_ADDRESS_MAP.put(servicePrimaryName,addressActive);
    }

    @Override
    public List<T> addressFilter(String providerAppName, String servicePrimaryName,String routeId) {
        AddressActive<T> addressActive = SERVICE_REMOTE_ADDRESS_MAP.get(servicePrimaryName);
       if (addressActive == null){
           return null;
       }
        String resourceType = addressActive.getResourceType();
        Map<String, List<T>> unitServersMap = addressActive.getUnitServersMap();
        List<T> originalList = addressActive.getOriginalList();

        List<T> result = getFilterResult(servicePrimaryName,resourceType,unitServersMap,originalList,routeId);

        logServer("route result afterZeroFilterServerList:", result);
        return result;
    }

    @Override
    public List<T> addressFilter(String providerAppName, String servicePrimaryName, List<T> list,String routeId) {
        if (this.middleWareTypeEnum == null || this.rpcUnitCellCallBack == null || StringUtils.isBlank(providerAppName)|| StringUtils.isBlank(servicePrimaryName)){
            return list;
        }

        // 1.
        String resourceType = getResourceType(servicePrimaryName,list);

        // unit: originalServers
        Map<String, List<T>> unitServersMap = transToUnitFlagServerListMap(list);

        // 2. 优先及单元化处理过滤
        List<T> result = getFilterResult(servicePrimaryName,resourceType,unitServersMap,list,routeId);

        logServer("route result afterZeroFilterServerList:", result);
        return result;
    }


    private List<T> getFilterResult(String servicePrimaryName,String resourceType,Map<String, List<T>> unitServersMap,  List<T> originalServers,String routeId) {
        if (StringUtils.isBlank(resourceType) || resourceType.equalsIgnoreCase(ResourceActiveType.NORMAL_RESOURCE_TYPE)) {
            /** 普通服务 或 是未单元化的服务 */
            return commonServers(unitServersMap, originalServers);
        }
        if (resourceType.equalsIgnoreCase(ResourceActiveType.CENTER_RESOURCE_TYPE)){
            return centerServers(unitServersMap,servicePrimaryName);
        }

        if (resourceType.equalsIgnoreCase(ResourceActiveType.UNIT_RESOURCE_TYPE)) {
            return unitServers(unitServersMap, servicePrimaryName,routeId);
        }

        // 不在上述当中，默认为普通服务
        return commonServers(unitServersMap, originalServers);
    }

    private List<T> unitServers(Map<String, List<T>> unitServersMap, String servicePrimaryName,String routeId) {
        if (routeId == null){
            // 显示透传没有，则取隐 线程上下文
            routeId = AppContextClient.getRouteId();
        }
        if (routeId == null) {
            // 无routeId 在多活里面 直接报错，无单元化路由目标地址，
            String msg = MessageFormat.format("service:{0},not have routeId",servicePrimaryName);
            logger.error(msg);
            throw new AppactiveException(msg);
        }

        String targetUnit = trafficRouteRuleService.getUnitByRouteId(routeId);
        if (StringUtils.isBlank(targetUnit)) {
            String msg = MessageFormat.format("service:{0},routeId:{1},targetUnit is null",servicePrimaryName,routeId);
            logger.error(msg);
            throw new AppactiveException(msg);
        }

        targetUnit = targetUnit.toUpperCase();
        List<T> unitServers = unitServersMap.get(targetUnit);
        if (CollectionUtils.isEmpty(unitServers)) {
            /* 单元地址池没有目标单元的地址，不进行兜底*/
            String msg = MessageFormat.format("service:{0},routeId:{1},targetUnit:{2},list is null",servicePrimaryName,routeId,targetUnit);
            logger.error(msg);
            throw new AppactiveException(msg);
        }

        return unitServers;
    }

    private List<T> centerServers(Map<String, List<T>> unitServersMap, String servicePrimaryName) {
        String centerFlag = AppactiveConstant.CENTER_FLAG.toUpperCase();
        List<T> invokers = unitServersMap.get(centerFlag);
        if (CollectionUtils.isEmpty(invokers)){
            // 无中心服务
            String msg = MessageFormat.format("service:{0},not have center list",servicePrimaryName);
            logger.error(msg);
            /* 说明代码哪里有问题, 或者用户配置错误了writeMode*/
            throw new AppactiveException(msg);
        }

        return invokers;
    }

    private List<T> commonServers(Map<String, List<T>> unitServersMap, List<T> originalServers) {
        // consumer 本机所在单元
        String currentUnit = machineUnitRuleService.getCurrentUnit();
        if (currentUnit == null) {
            // 1 不存在单元标，切0后，随机调用下游
            return originalServers;
        }
        // 2 存在单元标
        currentUnit = currentUnit.toUpperCase();

        List<T> currentUnitServers = unitServersMap.get(currentUnit);
        if (CollectionUtils.isEmpty(currentUnitServers)) {
            // 2-1. 本单元没机器，则随机全局调用
            return originalServers;
        }

        // 单元内优先调用
        return currentUnitServers;
    }

    private Map<String, List<T>> transToUnitFlagServerListMap(List<T> servers) {
        Map<String, List<T>> unitServersMap = new ConcurrentHashMap<String, List<T>>();

        for (T server : servers) {
            // get unitName
            String unitName = NO_UNIT_LABEL_PROVIDER_TAG_NAME;
            String metaUnitValue = getMetaMapFromServer(server, RPCConstant.URL_UNIT_LABEL_KEY);
            if (StringUtils.isNotBlank(metaUnitValue)) {
                // meta have value
                unitName = metaUnitValue;
            }
            unitName = unitName.toUpperCase();

            // add server
            List<T> currentUnitServers = unitServersMap.computeIfAbsent(unitName, k -> new ArrayList<>());
            currentUnitServers.add(server);
        }
        return unitServersMap;
    }


    private String getMetaMapFromServer(T server,String key) {
        if (StringUtils.isBlank(key)){
            return null;
        }
        return rpcUnitCellCallBack.getMetaMapValue(server,key);
    }


    private String getResourceType(String servicePrimaryName, List<T> list) {
        if (CollectionUtils.isEmpty(list)){
            return null;
        }
        for (T invoker : list) {
            String metaMapValue = rpcUnitCellCallBack.getMetaMapValue(invoker,
                RPCConstant.URL_RESOURCE_ACTIVE_LABEL_KEY);
            if (StringUtils.isNotBlank(metaMapValue)){
                return metaMapValue;
            }
        }
        return null;
    }


    private void logServer(String prefix, List<T> servers) {
        if (CollectionUtils.isEmpty(servers)) {
            return;
        }
        List<String> toLog = new ArrayList<>();
        for (T server : servers) {
            String serverStr = rpcUnitCellCallBack.getServerToString(server);
            if (serverStr == null){
                continue;
            }
            toLog.add(serverStr);
        }
        logger.info(prefix + "{}", toLog);
    }
}
