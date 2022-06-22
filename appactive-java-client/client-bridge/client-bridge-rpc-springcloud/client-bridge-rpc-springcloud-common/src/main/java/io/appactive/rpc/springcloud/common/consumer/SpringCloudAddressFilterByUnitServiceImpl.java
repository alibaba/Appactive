package io.appactive.rpc.springcloud.common.consumer;

import com.alibaba.fastjson.JSON;
import io.appactive.java.api.base.constants.ResourceActiveType;
import io.appactive.java.api.base.enums.MiddleWareTypeEnum;
import io.appactive.java.api.bridge.rpc.constants.constant.RPCConstant;
import io.appactive.java.api.utils.lang.StringUtils;
import io.appactive.rpc.base.consumer.RPCAddressFilterByUnitServiceImpl;
import io.appactive.rpc.springcloud.common.ServiceMeta;
import io.appactive.rpc.springcloud.common.utils.Util;
import io.appactive.support.lang.CollectionUtils;
import io.appactive.support.log.LogUtil;
import org.slf4j.Logger;
import org.springframework.util.AntPathMatcher;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static io.appactive.rpc.springcloud.common.utils.Util.*;

/**
 */
public class SpringCloudAddressFilterByUnitServiceImpl<T> extends RPCAddressFilterByUnitServiceImpl<T> {

    private static final Logger logger = LogUtil.getLogger();
    private static final String EMPTY_MATCHER = "";

    private final AntPathMatcher antPathMatcher;

    private final Map<String, String> URI_TO_BM = new ConcurrentHashMap<>();

    public SpringCloudAddressFilterByUnitServiceImpl(MiddleWareTypeEnum middleWareTypeEnum) {
        super(middleWareTypeEnum);
        this.antPathMatcher = new AntPathMatcher();
    }

    private String getBestMatcher(Set<String> candidates, String nameOfTarget){
        String bestMatcher = EMPTY_MATCHER;
        if (CollectionUtils.isEmpty(candidates)){
            emptyCache(null, nameOfTarget);
            return bestMatcher;
        }
        if (URI_TO_BM.containsKey(nameOfTarget)){
            return URI_TO_BM.get(nameOfTarget);
        }
        bestMatcher = calcBestMatcher(antPathMatcher,candidates,nameOfTarget);
        if (!EMPTY_MATCHER.equals(bestMatcher)){
            // only store it when it`s not empty
            URI_TO_BM.put(nameOfTarget, bestMatcher);
        }
        return bestMatcher;

    }

    @Override
    public String getResourceType(String servicePrimaryName, List<T> list, String version) {
        Set<String> candidates = getCachedServicePrimaryNames();
        String bestMatcher  = getBestMatcher(candidates, servicePrimaryName);
        // todo need to cache
        for (T t : list) {
            // String v = super.rpcUnitCellCallBack.getMetaMapValue(t, RPCConstant.SPRING_CLOUD_SERVICE_META_VERSION);
            String meta = super.rpcUnitCellCallBack.getMetaMapValue(t, RPCConstant.SPRING_CLOUD_SERVICE_META);
            if (StringUtils.isBlank(meta)){
                continue;
            }
            List<ServiceMeta> serviceMetaList = JSON.parseArray(meta, ServiceMeta.class);
            if (CollectionUtils.isEmpty(serviceMetaList)){
                continue;
            }
            for (ServiceMeta serviceMeta : serviceMetaList) {
                if (serviceMeta.getUriPrefix().equals(bestMatcher)){
                    return serviceMeta.getRa();
                }
            }
        }
        return ResourceActiveType.NORMAL_RESOURCE_TYPE;
    }


    public static String calcBestMatcher(AntPathMatcher antPathMatcher, Set<String> candidates, String nameOfTarget){
        //  org.springframework.web.servlet.handler.AbstractUrlHandlerMapping#lookupHandler(java.lang.String, javax.servlet.http.HttpServletRequest)
        String bestMatcher = EMPTY_MATCHER;
        List<String> matchingPatterns = new ArrayList<>();
        String targetUri = getUriFromPrimaryName(nameOfTarget);
        Set<String> candidateUris = candidates.stream().map(Util::getUriFromPrimaryName).collect(Collectors.toSet());
        for (String candidateUri : candidateUris) {
            if (antPathMatcher.match(candidateUri, targetUri)) {
                matchingPatterns.add(candidateUri);
            }
        }
        Comparator<String> patternComparator = antPathMatcher.getPatternComparator(targetUri);
        if (!matchingPatterns.isEmpty()) {
            matchingPatterns.sort(patternComparator);
            if (logger.isDebugEnabled()) {
                logger.debug("Matching patterns for request [" + targetUri + "] are " + matchingPatterns);
            }
            bestMatcher = matchingPatterns.get(0);
        }
        bestMatcher = buildServicePrimaryName(getAppNameFromPrimaryName(nameOfTarget),bestMatcher);
        return bestMatcher;
    }

    @Override
    public List<T> addressFilter(String providerAppName, String servicePrimaryName,String routeId) {
        Set<String> candidates = getCachedServicePrimaryNames();
        /// in fact, pre-confined meta usually doesn`t contain a specific uri, it`s better to just skip testing and calculate matcher
        // String bestMatcher = servicePrimaryName;
        // if (!candidates.contains(servicePrimaryName)){
        //     bestMatcher = bestMatcher(candidates, servicePrimaryName);
        // }
        String bestMatcher  = getBestMatcher(candidates, servicePrimaryName);
        logger.info("candidates {}, servicePrimaryName {}, bestMatcher {}",candidates, servicePrimaryName, bestMatcher);
        return super.addressFilter(providerAppName, bestMatcher, routeId);
    }

    @Override
    public Boolean emptyCache(String providerAppName, String servicePrimaryName) {
        super.emptyCache(providerAppName, servicePrimaryName);
        URI_TO_BM.remove(servicePrimaryName);
        return true;
    }
}
