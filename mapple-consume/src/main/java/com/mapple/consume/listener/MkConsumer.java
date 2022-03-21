package com.mapple.consume.listener;

import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

@RocketMQMessageListener(topic = "MkOrder-Topic", consumerGroup = "${rocketmq.consumer.group}")
@Component
public class MkConsumer implements RocketMQListener<String> {

    @Override
    public void onMessage(String s) {
        System.out.println("接收到消息" + s);
    }
}
