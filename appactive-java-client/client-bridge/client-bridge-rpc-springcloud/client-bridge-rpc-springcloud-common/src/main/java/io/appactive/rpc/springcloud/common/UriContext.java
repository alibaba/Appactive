package io.appactive.rpc.springcloud.common;

/**
 *
 * @author mageekchiu
 */
public class UriContext {

    private static final ThreadLocal<String> URI_INFO = new ThreadLocal<String>();

    public static void setUriPath(String targetUnit) {
        URI_INFO.set(targetUnit);
    }

    public static void clearContext() {
        URI_INFO.remove();
    }

    public static String getUriPath() {
        return URI_INFO.get();
    }
}
