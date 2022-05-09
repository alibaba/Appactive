package io.appactive.rpc.springcloud.common.consumer;

import com.alibaba.fastjson.JSON;
import io.appactive.java.api.base.constants.ResourceActiveType;
import io.appactive.java.api.base.enums.MiddleWareTypeEnum;
import io.appactive.java.api.bridge.rpc.constants.constant.RPCConstant;
import io.appactive.java.api.utils.lang.StringUtils;
import io.appactive.rpc.base.consumer.RPCAddressFilterByUnitServiceImpl;
import io.appactive.rpc.springcloud.common.ServiceMeta;
import io.appactive.rpc.springcloud.common.UriContext;
import io.appactive.support.lang.CollectionUtils;
import org.springframework.util.AntPathMatcher;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 */
public class SpringCloudAddressFilterByUnitServiceImpl<T> extends RPCAddressFilterByUnitServiceImpl<T> {

    private final AntPathMatcher antPathMatcher;
    private final String unExistedVersion = "-1.111";

    private final Map<String, String> URI_TO_RA = new ConcurrentHashMap<>();
    private final Map<String, String> URI_TO_RA_VERSION = new ConcurrentHashMap<>();

    public SpringCloudAddressFilterByUnitServiceImpl(MiddleWareTypeEnum middleWareTypeEnum) {
        super(middleWareTypeEnum);
        this.antPathMatcher = new AntPathMatcher();
    }

    @Override
    public String getResourceType(String servicePrimaryName, List<T> list, String version) {
        if (CollectionUtils.isEmpty(list)){
            return null;
        }
        String uri = UriContext.getUriPath();
        if (URI_TO_RA_VERSION.getOrDefault(servicePrimaryName, unExistedVersion).equalsIgnoreCase(version)){
            if (URI_TO_RA.containsKey(servicePrimaryName)){
                return URI_TO_RA.get(servicePrimaryName);
            }
        }

        for (T invoker : list) {
            String metaMapValue = rpcUnitCellCallBack.getMetaMapValue(invoker, RPCConstant.SPRING_CLOUD_SERVICE_META);
            if (StringUtils.isNotBlank(metaMapValue)){
                List<ServiceMeta> serviceMetaList = JSON.parseArray(metaMapValue,ServiceMeta.class);
                if (CollectionUtils.isNotEmpty(serviceMetaList)){
                    for (ServiceMeta serviceMeta : serviceMetaList) {
                        if (antPathMatcher.match(serviceMeta.getUriPrefix(),uri)){
                            String ra = serviceMeta.getRa();
                            updateMeta(servicePrimaryName, version, ra);
                            return ra;
                        }
                    }
                }
            }
        }
        // 没有标记，默认为是普通服务
        String ra = ResourceActiveType.NORMAL_RESOURCE_TYPE;
        updateMeta(servicePrimaryName, version, ra);
        return ra;
    }

    private void updateMeta(String servicePrimaryName, String version, String ra) {
        URI_TO_RA.put(servicePrimaryName, ra);
        if (version != null) {
            URI_TO_RA_VERSION.put(servicePrimaryName, version);
        }
    }
}
