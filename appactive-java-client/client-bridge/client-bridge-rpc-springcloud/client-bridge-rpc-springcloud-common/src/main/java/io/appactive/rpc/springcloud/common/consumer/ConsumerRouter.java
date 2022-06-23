package io.appactive.rpc.springcloud.common.consumer;


import com.alibaba.fastjson.JSON;
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

import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;
import java.util.List;

import static io.appactive.rpc.springcloud.common.utils.Util.buildServicePrimaryName;

/**
 * @author mageekchiu
 */
public class ConsumerRouter<T> {

    private static final Logger logger = LogUtil.getLogger();

    private final ServerMetaService<T> serverMeta ;
    private final SpringCloudAddressFilterByUnitServiceImpl<T> addressFilterByUnitService;

    public ConsumerRouter(Class<T> type) {
        String baseName = "io.appactive.rpc.springcloud.nacos.consumer.";
        String[] classes = new String[]{
                "NacosServerMeta",
                "EurekaServerMeta",
        };
        serverMeta = loadServerMeta(baseName, classes, type);
        if (serverMeta == null){
            String msg = MessageFormat.format("No available ServerMeta among classes: {0}",classes);
            throw ExceptionFactory.makeFault(msg);
        }else {
            logger.info("filter ServerMeta found: {}", serverMeta.getClass().getName());
            addressFilterByUnitService = new SpringCloudAddressFilterByUnitServiceImpl<>(MiddleWareTypeEnum.SPRING_CLOUD);
            addressFilterByUnitService.initAddressCallBack(new SpringCloud2AddressCallBack<>(serverMeta));
        }
    }

    private ServerMetaService<T> loadServerMeta(String baseName, String[] classNames, Class<T> type){
        for (String className : classNames) {
            try {
                Class clazz = Class.forName(baseName+className);
                Class[] classArgs = new Class[1];
                classArgs[0] = Class.class;
                return (ServerMetaService<T>)clazz.getDeclaredConstructor(classArgs).newInstance(type);
                // return (ServerMetaService<T>)clazz.newInstance();
            }catch (ClassNotFoundException | IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException e) {

            }
        }
        return null;
    }


    public List<T> filterWithoutCache(List<T> servers){
        if (CollectionUtils.isEmpty(servers)){
            return servers;
        }
        T oneServer = servers.get(0);
        String appName = serverMeta.getAppName(oneServer);
        String servicePrimaryKey = buildServicePrimaryName(appName,UriContext.getUriPath());
        List<T> list = addressFilterByUnitService.addressFilter(null, servicePrimaryKey, servers, AppContextClient.getRouteId());
        return list;
    }


    /**
     * return qualified server subset from origin list
     * @param servers origin server list
     * @return qualified server list
     */
    public List<T> filter(List<T> servers){
        if (CollectionUtils.isEmpty(servers)){
            return servers;
        }
        T oneServer = servers.get(0);
        String appName = serverMeta.getAppName(oneServer);
        String servicePrimaryKey = buildServicePrimaryName(appName,UriContext.getUriPath());

        /// We all ready make sure all service stored through ConsumerRouter.refresh and URIRegister.doRegisterUris,
        // so there is no need to call method bellow
        // addressFilterByUnitService.refreshAddressList(null, servicePrimaryKey, servers, version);
        List<T> list = addressFilterByUnitService.addressFilter(null, servicePrimaryKey, AppContextClient.getRouteId());
        return list;
    }


    /**
     * server size changes or server meta changes.
     * @param servers new servers
     * @return updated number
     */
    public synchronized Integer refresh(List<T> servers){
        Integer changed = 0;
        if (CollectionUtils.isEmpty(servers)){
            return changed;
        }
        T oneServer = servers.get(0);
        String appName = serverMeta.getAppName(oneServer);
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
            if(addressFilterByUnitService.refreshAddressList(null, servicePrimaryKey, servers, version, serviceMeta.getRa())){
                changed++;
            }
        }
        return changed;
    }
}
