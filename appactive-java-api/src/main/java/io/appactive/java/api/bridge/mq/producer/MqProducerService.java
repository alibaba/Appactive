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
