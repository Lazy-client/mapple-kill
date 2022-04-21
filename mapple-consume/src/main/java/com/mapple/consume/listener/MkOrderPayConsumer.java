package com.mapple.consume.listener;

import com.alibaba.fastjson.JSON;
import com.mapple.common.utils.RocketMQConstant;
import com.mapple.common.vo.MkOrderPay;
import com.mapple.consume.service.MkOrderService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListener;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQPushConsumerLifecycleListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RocketMQMessageListener(
        topic = RocketMQConstant.Topic.payTopic,
        consumerGroup = RocketMQConstant.ConsumerGroup.payConsumerGroup)
@Component
@Slf4j
public class MkOrderPayConsumer implements MessageListener, RocketMQPushConsumerLifecycleListener {


    @Resource
    private MkOrderService orderService;

    /**
     * ①扣减真正的库存
     * ②扣减自己的余额
     * ③增加公共账户余额
     * ④设置订单状态为1
     */

    @Override
    public void prepareStart(DefaultMQPushConsumer defaultMQPushConsumer) {
        // 每次拉取的间隔，单位为毫秒
        defaultMQPushConsumer.setPullInterval(750);
        // 设置每次从队列中拉取的消息数为16
        defaultMQPushConsumer.setPullBatchSize(256);
    }

    
}