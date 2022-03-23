package com.mapple.consume.listener;

import com.mapple.consume.service.MkOrderService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@RocketMQMessageListener(
        topic = "MkOrder-Topic",
        consumerGroup = "${rocketmq.consumer.group}")   // 负载均衡
@Component
@Slf4j
public class MkOrderConsumer implements RocketMQListener<MessageExt> {

    @Resource
    private MkOrderService orderService;

    @Override
    public void onMessage(MessageExt messageExt) {
//        String body = new String(messageExt.getBody(), StandardCharsets.UTF_8);
//        log.info("接受消息成功");
//        MkOrder order = JSON.parseObject(body, MkOrder.class);
//        boolean flag = orderService.save(order);
//        if (!flag)
//            throw new ConsumeException(400, "生成订单失败");
//        log.info("生成订单成功");
    }
}
