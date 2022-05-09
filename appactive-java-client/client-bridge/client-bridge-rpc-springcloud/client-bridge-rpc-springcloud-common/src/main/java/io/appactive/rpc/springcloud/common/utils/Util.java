package io.appactive.rpc.springcloud.common.utils;

import io.appactive.support.lang.CollectionUtils;
import org.springframework.util.AntPathMatcher;

import java.util.HashSet;
import java.util.Set;

/**
 */
public class Util {

    private static String split = "@active@";

    private static final AntPathMatcher antPathMatcher = new AntPathMatcher();


    public static String buildServicePrimaryName(String appName, String uriPrefix){
        return appName + split + uriPrefix;
    }

    public static String getUriFromPrimaryName(String servicePrimaryName){
        return servicePrimaryName.split(split)[1];
    }


    public static String unExistedVersion = "-1.111";

    public static String bestMatcher(Set<String> candidates, String target){
        String bestMatcher = "";
        if (CollectionUtils.isEmpty(candidates)){
            return bestMatcher;
        }
        for (String candidate : candidates) {
            if (antPathMatcher.match(getUriFromPrimaryName(candidate), getUriFromPrimaryName(target))){
                if (candidate.length()>bestMatcher.length()){
                    bestMatcher = candidate;
                }
            }
        }
        return bestMatcher;
    }

    public static void main(String... args){
        bestMatcher(new HashSet<String>(){{
            add("DEFAULT_GROUP@@product@active@/detail/*");
            add("DEFAULT_GROUP@@product@active@/detailHidden/*");
            add("DEFAULT_GROUP@@product@active@/**");
            add("DEFAULT_GROUP@@product@active@/buy/*");
        }},"DEFAULT_GROUP@@product@active@/list/");
    }

}
