package com.mapple.consume.listener;

import com.alibaba.fastjson.JSON;
import com.mapple.consume.entity.MkOrder;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@RocketMQMessageListener(
        topic = "${mq.order.producer.topic}",
        consumerGroup = "${mq.order.consumer.group}")   // 负载均衡
@Component
@Slf4j
public class MkOrderTransactionConsumer implements RocketMQListener<MessageExt> {

    @Override
    public void onMessage(MessageExt messageExt) {
        String body = new String(messageExt.getBody(), StandardCharsets.UTF_8);
        log.info("事务消费者接受消息成功......");
        MkOrder order = JSON.parseObject(body, MkOrder.class);
        log.info("生成事务订单成功, 消息id: {}, 订单id: {}", messageExt.getMsgId(), order.getId());
    }
}
