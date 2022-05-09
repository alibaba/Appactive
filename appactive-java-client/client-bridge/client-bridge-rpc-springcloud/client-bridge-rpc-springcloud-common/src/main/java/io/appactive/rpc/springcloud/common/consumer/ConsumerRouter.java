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
import java.util.Calendar;
import java.util.List;

import static io.appactive.rpc.springcloud.common.utils.Util.buildServicePrimaryName;

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
        String servicePrimaryKey = buildServicePrimaryName(appName,UriContext.getUriPath());

        /// We all ready make sure all service stored through ConsumerRouter.refresh and URIRegister.doRegisterUris,
        // so there is no need to call method bellow
        // addressFilterByUnitService.refreshAddressList(null, servicePrimaryKey, servers, version);
        List<Server> list = addressFilterByUnitService.addressFilter(null, servicePrimaryKey, AppContextClient.getRouteId());
        return list;
    }


    /**
     * server size changes or server meta changes.
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
        if (StringUtils.isBlank(metaMapValue)){
            return changed;
        }
        List<ServiceMeta> serviceMetaList = JSON.parseArray(metaMapValue,ServiceMeta.class);
        if (CollectionUtils.isEmpty(serviceMetaList)){
            return changed;
        }
        for (ServiceMeta serviceMeta : serviceMetaList) {
            String servicePrimaryKey = buildServicePrimaryName(appName, serviceMeta.getUriPrefix());
            if(addressFilterByUnitService.refreshAddressList(null, servicePrimaryKey, servers, version)){
                changed++;
            }
        }
        return changed;
    }
}
