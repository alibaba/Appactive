package io.appactive.rpc.springcloud.common.utils;

/**
 */
public class Util {

    private static String split = "@active@";

    public static String buildServicePrimaryName(String appName, String uriPrefix){
        return appName + split + uriPrefix;
    }

    public static String getUriFromPrimaryName(String servicePrimaryName){
        return servicePrimaryName.split(split)[1];
    }


    public static String unExistedVersion = "-1.111";

}
