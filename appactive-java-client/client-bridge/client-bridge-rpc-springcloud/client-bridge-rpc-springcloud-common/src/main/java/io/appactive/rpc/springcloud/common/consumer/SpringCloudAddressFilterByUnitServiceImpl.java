package io.appactive.rpc.springcloud.common.consumer;

import com.alibaba.fastjson.JSON;
import io.appactive.java.api.base.constants.ResourceActiveType;
import io.appactive.java.api.base.enums.MiddleWareTypeEnum;
import io.appactive.java.api.bridge.rpc.constants.constant.RPCConstant;
import io.appactive.java.api.utils.lang.StringUtils;
import io.appactive.rpc.base.consumer.RPCAddressFilterByUnitServiceImpl;
import io.appactive.rpc.base.consumer.bo.AddressActive;
import io.appactive.rpc.springcloud.common.ServiceMeta;
import io.appactive.rpc.springcloud.common.UriContext;
import io.appactive.support.lang.CollectionUtils;
import io.appactive.support.log.LogUtil;
import org.slf4j.Logger;
import org.springframework.util.AntPathMatcher;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static io.appactive.rpc.springcloud.common.utils.Util.*;

/**
 */
public class SpringCloudAddressFilterByUnitServiceImpl<T> extends RPCAddressFilterByUnitServiceImpl<T> {

    private static final Logger logger = LogUtil.getLogger();

    private final AntPathMatcher antPathMatcher;

    private final Map<String, String> URI_TO_RA = new ConcurrentHashMap<>();
    private final Map<String, String> URI_TO_RA_VERSION = new ConcurrentHashMap<>();

    public SpringCloudAddressFilterByUnitServiceImpl(MiddleWareTypeEnum middleWareTypeEnum) {
        super(middleWareTypeEnum);
        this.antPathMatcher = new AntPathMatcher();
    }

    @Override
    public String getResourceType(String servicePrimaryName, List<T> list, String version) {
        if (CollectionUtils.isEmpty(list)){
            emptyCache(null, servicePrimaryName);
            return null;
        }
        String uri = getUriFromPrimaryName(servicePrimaryName);
        if (URI_TO_RA_VERSION.getOrDefault(servicePrimaryName, unExistedVersion).equalsIgnoreCase(version)){
            if (URI_TO_RA.containsKey(servicePrimaryName)){
                return URI_TO_RA.get(servicePrimaryName);
            }
        }

        String bestMatcher = "";
        String ra  = "";
        for (T invoker : list) {
            String metaMapValue = getMetaMapFromServer(invoker, RPCConstant.SPRING_CLOUD_SERVICE_META);
            if (StringUtils.isNotBlank(metaMapValue)){
                List<ServiceMeta> serviceMetaList = JSON.parseArray(metaMapValue,ServiceMeta.class);
                if (CollectionUtils.isNotEmpty(serviceMetaList)){
                    for (ServiceMeta serviceMeta : serviceMetaList) {
                        if (antPathMatcher.match(serviceMeta.getUriPrefix(),uri)){
                            if (serviceMeta.getUriPrefix().length() > bestMatcher.length()){
                                bestMatcher = serviceMeta.getUriPrefix();
                                ra = serviceMeta.getRa();
                            }
                        }
                    }
                }
            }
        }
        if (StringUtils.isNotBlank(bestMatcher)){
            updateMeta(servicePrimaryName, version, ra);
            return ra;
        }
        logger.error("no  meta for uri {}, fallback to default normal", servicePrimaryName);
        // 没有标记，默认为是普通服务. 事实上 不可能走到这里。
        ra = ResourceActiveType.NORMAL_RESOURCE_TYPE;
        updateMeta(servicePrimaryName, version, ra);
        return ra;
    }

    @Override
    public List<T> addressFilter(String providerAppName, String servicePrimaryName,String routeId) {
        Set<String> candidates = getCachedServicePrimaryNames();
        if (candidates.contains(servicePrimaryName)){
            return super.addressFilter(providerAppName, servicePrimaryName, routeId);
        }
        String bestMatcher = bestMatcher(candidates, servicePrimaryName);
        logger.info("candidates {},servicePrimaryName {},bestMatcher {}",candidates,servicePrimaryName,bestMatcher);
        return super.addressFilter(providerAppName, bestMatcher, routeId);
    }

    @Override
    public Boolean emptyCache(String providerAppName, String servicePrimaryName) {
        super.emptyCache(providerAppName, servicePrimaryName);
        URI_TO_RA.remove(servicePrimaryName);
        URI_TO_RA_VERSION.remove(servicePrimaryName);
        return true;
    }

    private void updateMeta(String servicePrimaryName, String version, String ra) {
        URI_TO_RA.put(servicePrimaryName, ra);
        if (version != null) {
            URI_TO_RA_VERSION.put(servicePrimaryName, version);
        }
    }
}
