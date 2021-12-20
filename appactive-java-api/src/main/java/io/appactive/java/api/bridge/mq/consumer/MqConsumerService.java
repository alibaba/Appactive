package io.appactive.java.api.bridge.mq.consumer;

import io.appactive.java.api.bridge.mq.message.MessageUnitService;
import io.appactive.java.api.bridge.mq.constants.enums.MQSubChainStatusEnum;

public interface MqConsumerService<T> {

    MQSubChainStatusEnum consume(MessageUnitService<T> message);
}
