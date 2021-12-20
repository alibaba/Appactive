package io.appactive.java.api.base.exception;

import io.appactive.java.api.base.constants.AppactiveConstant;
import io.appactive.java.api.base.exception.bo.IMsg;
import io.appactive.java.api.utils.lang.StringUtils;

/**
 */
public class AppactiveException extends RuntimeException {

    /**   */
    private static final long serialVersionUID = 1L;

    /** Key */
    protected String          code;

    /** Error Message */
    protected String          msg;

    /** 针对当前异常附件的业务上下文数据 */
    protected Object          context;

    public AppactiveException(String message){
        super(message);
    }

    /**
     * @param code
     * @param msg
     */
    public AppactiveException(String code, String msg){
        this.code = code;
        this.msg = msg;
    }

    /**
     * @param code
     * @param msg
     * @param tr
     */
    public AppactiveException(String code, String msg, Throwable tr){
        super(tr);
        this.code = code;
        this.msg = msg;
    }

    /**
     * @see Throwable#getMessage()
     */
    @Override
    public String getMessage() {
        StringBuilder strBuilder = new StringBuilder();
        strBuilder.append(AppactiveConstant.PROJECT_NAME);
        strBuilder.append("-Exception:");
        strBuilder.append(this.code);
        strBuilder.append("|");
        strBuilder.append(this.msg);
        if (StringUtils.isNotBlank(super.getMessage())) {
            strBuilder.append("||SuperMessage:");
            strBuilder.append(super.getMessage());
        }
        return strBuilder.toString();
    }

    /**
     * 取得简短异常信息提示
     * 
     * @return
     */
    public String getShortMessage() {
        StringBuffer message = new StringBuffer(this.getMsg());
        if (StringUtils.isNotBlank(super.getMessage())) {
            message.append("|").append(super.getMessage());
        }
        return message.toString();
    }

    /**
     * 指定的信息是否与当前Exception是相同的
     * 
     * @param code
     * @return
     */
    public boolean isMatchCode(String code) {
        if (code == null) {
            return false;
        }

        return this.code.equals(code);
    }

    /**
     * 指定的信息是否与当前Exception是相同的
     *
     * @param msgInfo
     * @return
     */
    public boolean isMatchMsg(IMsg msgInfo) {
        if (msgInfo == null || this.code == null) {
            return false;
        }

        return this.code.equals(msgInfo.getKey());
    }

    /**
     * @return Returns the code.
     */
    public String getCode() {
        return code;
    }

    /**
     * @param code The code to set.
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * @return Returns the msg.
     */
    public String getMsg() {
        return msg;
    }

    /**
     * @param msg The msg to set.
     */
    public void setMsg(String msg) {
        this.msg = msg;
    }

    /**
     * 安全的获取上下文
     * 
     * @param <T>
     * @param contextType
     * @return
     */
    @SuppressWarnings("unchecked")
    public <T> T getContext(Class<T> contextType) {
        if (contextType.isInstance(this.context)) {
            return (T) context;
        }
        return null;
    }

    public Object getContext() {
        return context;
    }

    public void setContext(Object context) {
        this.context = context;
    }
}
