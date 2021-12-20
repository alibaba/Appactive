package io.appactive.java.api.bridge.mq.producer;

import io.appactive.java.api.bridge.mq.constants.enums.MQPubChainStatusEnum;
import io.appactive.java.api.bridge.mq.message.MessageAttributeService;
import io.appactive.java.api.bridge.mq.message.MessageUnitService;

public interface MqProducerService<T> {

    /**
     * add attribute
     * @param message
     */
    void addAttribute(T message, MessageAttributeService messageAttributeService);

    /**
     * judge message is legal
     * @param messageUnitService
     * @return
     */
    MQPubChainStatusEnum send(MessageUnitService<T> messageUnitService);
}
