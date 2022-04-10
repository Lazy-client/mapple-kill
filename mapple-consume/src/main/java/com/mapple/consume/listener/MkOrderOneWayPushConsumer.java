package com.mapple.consume.listener;

import com.mapple.common.utils.RocketMQConstant;
import com.mapple.consume.entity.MkOrder;
import com.mapple.consume.exception.ConsumeException;
import com.mapple.consume.service.MkOrderService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQPushConsumerLifecycleListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;


/**
 * @author : Gelcon
 * @date : 2022/3/21 20:04
 */
@RocketMQMessageListener(
        topic = "MkOrder-Topic",
        consumerGroup = RocketMQConstant.ConsumerGroup.consumerGroup)   // 负载均衡
@Component
@Slf4j
public class MkOrderOneWayPushConsumer implements RocketMQListener<MkOrder>, RocketMQPushConsumerLifecycleListener {

    @Resource
    private MkOrderService orderService;


    @Override
    public void onMessage(MkOrder order) {
//        boolean flag = orderService.save(order);
//        if (!flag)
//            throw new ConsumeException(400, "生成订单失败");
//        log.info("生成订单成功," + "用户id: {}", order.getUserId());
    }

    @Override
    public void prepareStart(DefaultMQPushConsumer defaultMQPushConsumer) {
//        // 每次拉取的间隔，单位为毫秒
//        defaultMQPushConsumer.setPullInterval(2000);
//        // 设置每次从队列中拉取的消息数为16
//        defaultMQPushConsumer.setPullBatchSize(16);
    }


}
