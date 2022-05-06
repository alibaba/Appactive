package io.appactive.rpc.springcloud.common.consumer;


import com.netflix.loadbalancer.Server;
import io.appactive.java.api.base.AppContextClient;
import io.appactive.java.api.base.enums.MiddleWareTypeEnum;
import io.appactive.java.api.bridge.rpc.consumer.RPCAddressFilterByUnitService;
import io.appactive.java.api.rule.TrafficMachineService;
import io.appactive.java.api.rule.machine.AbstractMachineUnitRuleService;
import io.appactive.java.api.rule.traffic.TrafficRouteRuleService;
import io.appactive.rpc.base.consumer.RPCAddressFilterByUnitServiceImpl;
import io.appactive.rpc.springcloud.common.UriContext;
import io.appactive.rpc.springcloud.common.consumer.callback.SpringCloud2AddressCallBack;
import io.appactive.rule.ClientRuleService;
import io.appactive.support.lang.CollectionUtils;
import io.appactive.support.log.LogUtil;
import org.slf4j.Logger;

import java.util.List;

/**
 * @author mageekchiu
 */
public class ConsumerRouter {

    private static final Logger logger = LogUtil.getLogger();


    private final TrafficRouteRuleService trafficRouteRuleService = ClientRuleService.getTrafficRouteRuleService();
    private final AbstractMachineUnitRuleService machineUnitRuleService = ClientRuleService.getMachineUnitRuleService();
    private final TrafficMachineService trafficMachineService = new TrafficMachineService(trafficRouteRuleService,
            machineUnitRuleService);

    private String servicePrimaryKey;


    /**
     * remove unqualified servers from origin list
     * @param servers origin server list
     */
    public static void filterOrigin(List<Server> servers){
        if (CollectionUtils.isEmpty(servers)){
            return;
        }
        // todo remove unqualified servers
    }

    /**
     * return qualified server subset from origin list
     * @param servers origin server list
     * @return qualified server list
     */
    public static List<Server> filter(List<Server> servers){
        if (CollectionUtils.isEmpty(servers)){
            return servers;
        }
        return servers;
        // todo filtering
        // String servicePrimaryKey = servers.get(0).getMetaInfo().getAppName() + UriContext.getUriPath();
        //
        // RPCAddressFilterByUnitService addressFilterByUnitService = new RPCAddressFilterByUnitServiceImpl<List<Server>>(MiddleWareTypeEnum.SPRING_CLOUD);
        // addressFilterByUnitService.initAddressCallBack(new SpringCloud2AddressCallBack());
        // addressFilterByUnitService.refreshAddressList(null, servicePrimaryKey, servers);
        //
        // List<Server> list = addressFilterByUnitService.addressFilter(null, servicePrimaryKey, AppContextClient.getRouteId());
        // return list;
    }
}
