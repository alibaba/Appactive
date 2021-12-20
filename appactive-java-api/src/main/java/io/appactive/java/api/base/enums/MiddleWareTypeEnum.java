package io.appactive.java.api.base.enums;

/**
 */
public enum MiddleWareTypeEnum {
    /**
     * open rocketmq
     */
    OPEN_ROCKETMQ("open_rocketmq"),
    DUBBO("dubbo"),
    ;

    private final String value;

    public String getValue() {
        return value;
    }

    MiddleWareTypeEnum(String value) {
        this.value = value;
    }

    public static MiddleWareTypeEnum of(String value){
        if (value == null){
            return null;
        }
        for (MiddleWareTypeEnum typeEnum : MiddleWareTypeEnum.values()) {
            if (typeEnum.getValue().equalsIgnoreCase(value)){
                return typeEnum;
            }
        }
        return null;
    }


    @Override
    public String toString() {
        return "MiddleWareTypeEnum{" +
            "value='" + value + '\'' +
            '}';
    }
}
