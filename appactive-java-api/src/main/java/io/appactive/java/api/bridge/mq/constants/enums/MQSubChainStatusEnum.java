package io.appactive.java.api.bridge.mq.constants.enums;

import java.util.HashSet;
import java.util.Set;

public enum MQSubChainStatusEnum {
    /**
     * 非多活流程，继续消费流程
     */
    /**
     * 非单元化Topic，继续消费
     */
    consumeTopicNoUnit(),
    /**
     * 非单元化Group，应用双活逻辑需继续细分 Topic: 单元化Topic、应用双活Topic、其他Topic Group: 单元化Group、应用双活Group、其他Group
     */
    consumeGroupNoUnit(),
    /**
     * 非单元化Group && 非应用双活Group，继续消费
     */
    consumeOtherGroup(),
    /**
     * 其他Topic && 应用双活Group，继续消费
     */
    consumeOtherTopicIsApplicationGroup(),
    /**
     * 单元化Topic && 应用双活Group，不消费
     */
    notConsumeUnitTopicIsApplicationGroup(),
    /**
     * 应用双活Topic && 应用双活Group，应用双活逻辑消费
     */
    consumeByApplication(),
    /**
     * 处于容灾状态，全量消费
     */
    consumeInDisasterState(),
    /**
     * 多活流程走通，继续消费
     */
    consumeByUnit(),

    /**
     * 进入死信号
     */
    deadMessage(),
    /**
     * 放弃消费本消息，本消息将不会提交给业务逻辑处理。
     */
    skipMessage(),
    /**
     * 当前不处理本消息，延后重新消费
     */
    reConsumeLater();

    private static final Set<MQSubChainStatusEnum> CONSUME_CONTINUE_SET = new HashSet<MQSubChainStatusEnum>();

    static {
        CONSUME_CONTINUE_SET.add(MQSubChainStatusEnum.consumeByUnit);
        CONSUME_CONTINUE_SET.add(MQSubChainStatusEnum.consumeByApplication);
        CONSUME_CONTINUE_SET.add(MQSubChainStatusEnum.consumeTopicNoUnit);
        CONSUME_CONTINUE_SET.add(MQSubChainStatusEnum.consumeGroupNoUnit);
        CONSUME_CONTINUE_SET.add(MQSubChainStatusEnum.consumeOtherGroup);
        CONSUME_CONTINUE_SET.add(MQSubChainStatusEnum.consumeInDisasterState);
        CONSUME_CONTINUE_SET.add(MQSubChainStatusEnum.consumeOtherTopicIsApplicationGroup);
    }

    public static boolean isConsume(MQSubChainStatusEnum statusEnum) {
        if (CONSUME_CONTINUE_SET.contains(statusEnum)) {
            return true;
        }

        return false;
    }

}
