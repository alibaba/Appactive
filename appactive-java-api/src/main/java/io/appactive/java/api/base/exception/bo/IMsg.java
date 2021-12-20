package io.appactive.java.api.base.exception.bo;

public interface IMsg {

    /**
     * 取得信息定义Key
     * 
     * @return
     */
    String getKey();

    /**
     * 取得当前信息对应的国际化文本
     * 
     * @return
     */
    String getMsg(Object... params);
}
