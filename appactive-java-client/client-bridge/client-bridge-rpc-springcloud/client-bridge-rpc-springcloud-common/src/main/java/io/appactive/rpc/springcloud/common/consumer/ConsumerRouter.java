package io.appactive.rpc.springcloud.common.consumer;


import com.alibaba.fastjson.JSON;
import com.netflix.loadbalancer.Server;
import io.appactive.java.api.base.AppContextClient;
import io.appactive.java.api.base.enums.MiddleWareTypeEnum;
import io.appactive.java.api.base.exception.ExceptionFactory;
import io.appactive.java.api.bridge.rpc.constants.constant.RPCConstant;
import io.appactive.java.api.utils.lang.StringUtils;
import io.appactive.rpc.springcloud.common.ServiceMeta;
import io.appactive.rpc.springcloud.common.UriContext;
import io.appactive.rpc.springcloud.common.consumer.callback.SpringCloud2AddressCallBack;
import io.appactive.support.lang.CollectionUtils;
import io.appactive.support.log.LogUtil;
import org.slf4j.Logger;

import java.text.MessageFormat;
import java.util.List;

/**
 * @author mageekchiu
 */
public class ConsumerRouter {

    private static final Logger logger = LogUtil.getLogger();

    private static final ServerMeta serverMeta ;
    private static final SpringCloudAddressFilterByUnitServiceImpl<Server> addressFilterByUnitService;

    static {
        String baseName = "io.appactive.rpc.springcloud.nacos.consumer.";
        String[] classes = new String[]{
                "NacosServerMeta",
                "EurekaServerMeta",
        };
        serverMeta = loadServerMeta(baseName, classes);
        if (serverMeta == null){
            String msg = MessageFormat.format("No available ServerMeta among classes: {0}",classes);
            throw ExceptionFactory.makeFault(msg);
        }else {
            logger.info("filter ServerMeta found: {}", serverMeta.getClass().getName());
            addressFilterByUnitService = new SpringCloudAddressFilterByUnitServiceImpl<>(MiddleWareTypeEnum.SPRING_CLOUD);
            addressFilterByUnitService.initAddressCallBack(new SpringCloud2AddressCallBack<>(serverMeta));
        }
    }

    private static ServerMeta loadServerMeta(String baseName, String[] classNames){
        for (String className : classNames) {
            try {
                Class clazz = Class.forName(baseName+className);
                return (ServerMeta)clazz.newInstance();
            }catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {

            }
        }
        return null;
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
        Server oneServer = servers.get(0);
        String appName = oneServer.getMetaInfo().getAppName();
        String version = serverMeta.getMetaMap(oneServer).get(RPCConstant.SPRING_CLOUD_SERVICE_META_VERSION);
        String servicePrimaryKey = appName + UriContext.getUriPath();

        addressFilterByUnitService.refreshAddressList(null, servicePrimaryKey, servers, version);
        List<Server> list = addressFilterByUnitService.addressFilter(null, servicePrimaryKey, AppContextClient.getRouteId());
        return list;
    }


    /**
     * we only care about server size changes here.
     * service(app+uri) changes will be taken care of in (io.appactive.rpc.springcloud.common.consumer.ConsumerRouter#filter(java.util.List))
     * @param servers new servers
     * @return updated number
     */
    public static synchronized Integer refresh(List<Server> servers){
        Integer changed = 0;
        if (CollectionUtils.isEmpty(servers)){
            return changed;
        }
        Server oneServer = servers.get(0);
        String appName = oneServer.getMetaInfo().getAppName();
        String version = serverMeta.getMetaMap(oneServer).get(RPCConstant.SPRING_CLOUD_SERVICE_META_VERSION);

        String metaMapValue = addressFilterByUnitService.getMetaMapFromServer(oneServer, RPCConstant.SPRING_CLOUD_SERVICE_META);
        if (StringUtils.isNotBlank(metaMapValue)){
            List<ServiceMeta> serviceMetaList = JSON.parseArray(metaMapValue,ServiceMeta.class);
            if (CollectionUtils.isNotEmpty(serviceMetaList)){
                for (ServiceMeta serviceMeta : serviceMetaList) {
                    String servicePrimaryKey = appName + serviceMeta.getUriPrefix();
                    if (servers.size() != addressFilterByUnitService.getCachedServerSize(null, servicePrimaryKey)){
                        addressFilterByUnitService.emptyCache(null, servicePrimaryKey);
                        addressFilterByUnitService.refreshAddressList(null, servicePrimaryKey, servers, version);
                        changed++;
                    }
                }
            }
        }
        return changed;
    }
}
