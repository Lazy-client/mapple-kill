package com.mapple.common.utils;

/**
 * @author : Gelcon
 * @date : 2022/4/10 11:12
 */
public interface RocketMQConstant {

    interface Topic {
        String topic = "MkOrder-Topic";
        String delayTopic = "MkOrder-Delay-Topic";
        String payTopic = "MkOrder-Pay-Topic";
        String transactionTopic = "MkOrder-Transaction-Topic";
    }

    interface Tag {
        String tag = "order";
    }

    interface ProducerGroup {
        String producerGroup = "MkOrder-Producer-Group";
        String transactionProducerGroup = "MkOrder-Transaction-Producer-Group";

    }

    interface ConsumerGroup {
        String consumerGroup = "MkOrder-Consumer-Group";
        String delayConsumerGroup = "MkOrder-Delay-Consumer-Group";
        String payConsumerGroup = "MkOrder-Pay-Consumer-Group";
    }
}
