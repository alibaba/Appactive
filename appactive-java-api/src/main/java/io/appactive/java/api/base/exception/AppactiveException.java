/*
 * Copyright 1999-2022 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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

    public AppactiveException(String code, String msg){
        this.code = code;
        this.msg = msg;
    }

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


    public String getShortMessage() {
        StringBuffer message = new StringBuffer(this.getMsg());
        if (StringUtils.isNotBlank(super.getMessage())) {
            message.append("|").append(super.getMessage());
        }
        return message.toString();
    }


    public boolean isMatchCode(String code) {
        if (code == null) {
            return false;
        }

        return this.code.equals(code);
    }


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
