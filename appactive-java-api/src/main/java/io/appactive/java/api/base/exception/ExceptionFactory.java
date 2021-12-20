package io.appactive.java.api.base.exception;

import io.appactive.java.api.base.exception.bo.IMsg;

/**
 * Exception factory
 */
public final class ExceptionFactory {

    private ExceptionFactory() {

    }

    public static AppactiveException makeFault(String msg) {
        AppactiveException exp = new AppactiveException("0000", msg);
        return exp;
    }

    public static AppactiveException makeFault(String msg, Throwable tr) {
        AppactiveException exp = new AppactiveException("0000", msg, tr);
        return exp;
    }

    /**
     * 直接构建异常
     *
     * @param code
     * @param msg
     * @return
     */
    public static AppactiveException makeFault(String code, String msg) {
        AppactiveException exp = new AppactiveException(code, msg);
        return exp;
    }

    /**
     * 直接构建异常（基于已存在的异常）
     *
     * @param code
     * @param msg
     * @param tr
     * @return
     */
    public static AppactiveException makeFault(String code, String msg, Throwable tr) {
        AppactiveException exp = new AppactiveException(code, msg, tr);
        return exp;
    }

    /**
     * 创建业务异常
     *
     * @param messageInfo
     * @param params
     * @return
     */
    public static AppactiveException makeFault(IMsg messageInfo, Object... params) {
        String message = messageInfo.getMsg(params);
        AppactiveException exp = new AppactiveException(messageInfo.getKey(), message);
        return exp;
    }

    /**
     * 创建业务异常
     *
     * @param msg
     * @return
     */
    public static AppactiveException makeFault(IMsg msg) {
        String message = msg.getMsg();
        AppactiveException exp = new AppactiveException(msg.getKey(), message);
        return exp;
    }

    /**
     * 创建具有异常对象的ServiceException
     *
     * @param msg
     * @param tr
     * @param params
     * @return
     */
    public static AppactiveException makeFault(IMsg msg, Throwable tr, Object... params) {
        String message = msg.getMsg(params);
        AppactiveException exp = new AppactiveException(msg.getKey(), message, tr);
        return exp;
    }

}
