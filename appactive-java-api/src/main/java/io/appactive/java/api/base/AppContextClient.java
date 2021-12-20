package io.appactive.java.api.base;


public class AppContextClient {

    /**
     * 存放单元信息在线程上下文中
     * 子线程也能拿到信息
     */
    private static final ThreadLocal<String> UNIT_INFO = new ThreadLocal<String>();


    /**
     * 赋值单元信息到线程上下文中
     * @param routeId 分流 id
     */
    public static void setUnitContext(String routeId) {
        if (routeId == null || "".equals(routeId)) {
            routeId = null;
        }

        UNIT_INFO.set(routeId);
    }

    /**
     * 清理线程上下文中的单元信息
     */
    public static void clearUnitContext(){
        UNIT_INFO.remove();
    }


    /**
     * 获得当前线程上下文的  单元化分流的用户维度id
     * @return
     */
    public static String getRouteId(){
        return UNIT_INFO.get();
    }

}
