package io.appactive.java.api.bridge.mq.message;

public interface MessageUnitService<T> {

    /**
     * topic have unit attribute?
     * @param message message obj
     * @return have? true:false
     */
    boolean isUnitTopic(T message);


    /**
     * group have unit attribute?
     * @param message message obj
     * @return have? true:false
     */
    boolean isUnitGroup(T message);


    /**
     * message route type
     * @param message message obj
     * @return route type
     */
    String getRouteType(T message);


    /**
     * message route id
     * @param message message obj
     * @return route id
     */
    String getRouteId(T message);


    /**
     * message produce unit
     * @param message message obj
     * @return produce unit
     */
    String getMsgUnit(T message);
}
