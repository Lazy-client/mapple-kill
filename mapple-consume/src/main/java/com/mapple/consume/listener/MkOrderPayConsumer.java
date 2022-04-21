package com.mapple.consume.listener;

import com.alibaba.fastjson.JSON;
import com.mapple.common.utils.RocketMQConstant;
import com.mapple.common.vo.MkOrderPay;
import com.mapple.consume.service.MkOrderService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;

@RocketMQMessageListener(
        topic = RocketMQConstant.Topic.payTopic,
        consumerGroup = RocketMQConstant.ConsumerGroup.payConsumerGroup)
@Component
@Slf4j
public class MkOrderPayConsumer implements RocketMQListener<MessageExt> {


    @Resource
    private MkOrderService orderService;

    /**
     * ①扣减真正的库存
     * ②扣减自己的余额
     * ③增加公共账户余额
     * ④设置订单状态为1
     */
    @Override
    public void onMessage(MessageExt messageExt) {
        String body = new String(messageExt.getBody(), StandardCharsets.UTF_8);
        log.info("接受消息成功");
        MkOrderPay pay = JSON.parseObject(body, MkOrderPay.class);
        // TODO 调用接口完成4个步骤
        // if (orderService.pay(pay))


    }
}
