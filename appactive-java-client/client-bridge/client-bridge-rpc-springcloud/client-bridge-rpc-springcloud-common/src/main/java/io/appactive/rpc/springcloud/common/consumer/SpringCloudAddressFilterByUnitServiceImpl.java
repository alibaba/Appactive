package io.appactive.rpc.springcloud.common.consumer;

import io.appactive.java.api.base.enums.MiddleWareTypeEnum;
import io.appactive.rpc.base.consumer.RPCAddressFilterByUnitServiceImpl;
import io.appactive.support.lang.CollectionUtils;
import io.appactive.support.log.LogUtil;
import org.slf4j.Logger;
import org.springframework.util.AntPathMatcher;

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

    private final Map<String, String> URI_TO_BM = new ConcurrentHashMap<>();

    public SpringCloudAddressFilterByUnitServiceImpl(MiddleWareTypeEnum middleWareTypeEnum) {
        super(middleWareTypeEnum);
        this.antPathMatcher = new AntPathMatcher();
    }

    private String bestMatcher(Set<String> candidates, String nameOfTarget){
        String bestMatcher = "";
        if (CollectionUtils.isEmpty(candidates)){
            emptyCache(null, nameOfTarget);
            return bestMatcher;
        }
        if (URI_TO_BM.containsKey(nameOfTarget)){
            return URI_TO_BM.get(nameOfTarget);
        }
        for (String candidate : candidates) {
            // todo: accommodate matching procedure to the one in spring its`self
            if (antPathMatcher.match(getUriFromPrimaryName(candidate), getUriFromPrimaryName(nameOfTarget))){
                if (candidate.length() > bestMatcher.length()){
                    bestMatcher = candidate;
                }
            }
        }
        updateMeta(nameOfTarget, bestMatcher);
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
        String bestMatcher  = bestMatcher(candidates, servicePrimaryName);
        logger.info("candidates {}, servicePrimaryName {}, bestMatcher {}",candidates, servicePrimaryName, bestMatcher);
        return super.addressFilter(providerAppName, bestMatcher, routeId);
    }

    @Override
    public Boolean emptyCache(String providerAppName, String servicePrimaryName) {
        super.emptyCache(providerAppName, servicePrimaryName);
        URI_TO_BM.remove(servicePrimaryName);
        return true;
    }

    private void updateMeta(String servicePrimaryName, String matcher) {
        URI_TO_BM.put(servicePrimaryName, matcher);
    }
}
