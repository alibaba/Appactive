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

import java.lang.annotation.ElementType;
import java.text.MessageFormat;
import java.util.*;
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
    public Boolean refreshAddressList(String providerAppName,String servicePrimaryName, List<T> list, String version, String resourceActive) {
        if (CollectionUtils.isEmpty(list)){
            emptyCache(providerAppName, servicePrimaryName);
        }
        if (version !=null && SERVICE_REMOTE_ADDRESS_MAP.containsKey(servicePrimaryName)){
            AddressActive<T> addressActive = SERVICE_REMOTE_ADDRESS_MAP.get(servicePrimaryName);
            if (list.equals(addressActive.getOriginalList())
                    && version.equalsIgnoreCase(getMetaMapFromServer(addressActive.getOriginalList().get(0),RPCConstant.SPRING_CLOUD_SERVICE_META_VERSION))
            ){
                // both servers and uris equals with current ones, no need to refresh
                return false;
            }
        }
        String resourceType = resourceActive == null ? getResourceType(servicePrimaryName, list, version) : resourceActive;
        Map<String, List<T>> unitServersMap = transToUnitFlagServerListMap(list);

        AddressActive<T> addressActive = new AddressActive<T>(resourceType,unitServersMap,list);

        SERVICE_REMOTE_ADDRESS_MAP.put(servicePrimaryName,addressActive);
        logger.info("caches of providerAppName:{}, servicePrimaryName:{} just got refreshed, new version:{}",providerAppName,servicePrimaryName,version);
        return true;
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

        logServer("server list after filtering:", result);
        return result;
    }

    @Override
    public List<T> addressFilter(String providerAppName, String servicePrimaryName, List<T> list,String routeId) {
        if (this.middleWareTypeEnum == null || this.rpcUnitCellCallBack == null || StringUtils.isBlank(providerAppName)|| StringUtils.isBlank(servicePrimaryName)){
            return list;
        }

        // 1.
        String resourceType = getResourceType(servicePrimaryName,list, null);

        // unit: originalServers
        Map<String, List<T>> unitServersMap = transToUnitFlagServerListMap(list);

        // 2. ??????????????????????????????
        List<T> result = getFilterResult(servicePrimaryName,resourceType,unitServersMap,list,routeId);

        logServer("route result afterZeroFilterServerList:", result);
        return result;
    }


    @Override
    public Boolean emptyCache(String providerAppName, String servicePrimaryName) {
        SERVICE_REMOTE_ADDRESS_MAP.remove(servicePrimaryName);
        return true;
    }


    private List<T> getFilterResult(String servicePrimaryName,String resourceType,Map<String, List<T>> unitServersMap,  List<T> originalServers,String routeId) {
        if (StringUtils.isBlank(resourceType) || ResourceActiveType.NORMAL_RESOURCE_TYPE.equalsIgnoreCase(resourceType)) {
            /** ???????????? ??? ???????????????????????? */
            return commonServers(unitServersMap, originalServers);
        }
        if (ResourceActiveType.CENTER_RESOURCE_TYPE.equalsIgnoreCase(resourceType)){
            return centerServers(unitServersMap,servicePrimaryName);
        }

        if (ResourceActiveType.UNIT_RESOURCE_TYPE.equalsIgnoreCase(resourceType)) {
            return unitServers(unitServersMap, servicePrimaryName,routeId);
        }

        // ??????????????????????????????????????????
        return commonServers(unitServersMap, originalServers);
    }

    private List<T> unitServers(Map<String, List<T>> unitServersMap, String servicePrimaryName,String routeId) {
        if (routeId == null){
            // ?????????????????????????????? ???????????????
            routeId = AppContextClient.getRouteId();
        }
        if (routeId == null) {
            // ???routeId ??????????????? ????????????????????????????????????????????????
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
            /* ????????????????????????????????????????????????????????????*/
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
            // ???????????????
            String msg = MessageFormat.format("service:{0},not have center list",servicePrimaryName);
            logger.error(msg);
            /* ???????????????????????????, ???????????????????????????writeMode*/
            throw new AppactiveException(msg);
        }

        return invokers;
    }

    private List<T> commonServers(Map<String, List<T>> unitServersMap, List<T> originalServers) {
        // consumer ??????????????????
        String currentUnit = machineUnitRuleService.getCurrentUnit();
        if (currentUnit == null) {
            // 1 ????????????????????????0????????????????????????
            logger.info("no unitFlag for current consumer, executing random calling");
            return originalServers;
        }
        // 2 ???????????????
        currentUnit = currentUnit.toUpperCase();

        List<T> currentUnitServers = unitServersMap.get(currentUnit);
        if (CollectionUtils.isEmpty(currentUnitServers)) {
            // 2-1. ??????????????????????????????????????????
            logger.info("no provider for current unit[{}] of consumer, executing random calling",currentUnit);
            return originalServers;
        }
        logger.info("executing current-unit-preferable calling");
        // ?????????????????????
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


    @Override
    public String getMetaMapFromServer(T server, String key) {
        if (StringUtils.isBlank(key)){
            return null;
        }
        return rpcUnitCellCallBack.getMetaMapValue(server,key);
    }

    @Override
    public Set<String> getCachedServicePrimaryNames() {
        return SERVICE_REMOTE_ADDRESS_MAP.keySet();
    }


    /**
     * getResourceType
     * @param servicePrimaryName as it is
     * @param list provider list
     * @param version version version of config itself, which can be used to reduce calculation.
     *                null means you need to calculate list every time
     * @return
     */
    protected String getResourceType(String servicePrimaryName, List<T> list, String version) {
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
