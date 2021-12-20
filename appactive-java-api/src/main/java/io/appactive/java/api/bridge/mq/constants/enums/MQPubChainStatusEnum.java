package io.appactive.java.api.bridge.mq.constants.enums;

public enum MQPubChainStatusEnum {

    /**
     * 非单元化的Topic，则应正常发送
     */
    notUnitTopic(),
    /**
     * 缺少多活参数，报错
     */
    lackParams(),
    /**
     * 单元化的 topic，发送
     */
    unitSend(),

}
