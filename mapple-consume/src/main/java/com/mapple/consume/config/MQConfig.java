package com.mapple.consume.config;

import com.mapple.common.utils.RocketMQConstant;
import com.mapple.consume.entity.MkOrder;
import com.mapple.consume.service.CouponFeignService;
import com.mapple.consume.service.MkOrderService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import com.alibaba.fastjson.JSONObject;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.springframework.context.annotation.Bean;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author : Gelcon
 * @date : 2022/3/21 21:28
 */
@Configuration
@Slf4j
@Data
public class MQConfig {

    // 注意都需要私有化
    @Value("${rocketmq.name-server}")
    private String nameServer;

    @Resource
    private MkOrderService orderService;

    @Resource
    private RocketMQTemplate rocketMQTemplate;

    @Resource
    private CouponFeignService couponFeignService;

    /**
     * 实现批量处理，消费对应主题和Tag的消息，然后调用批量处理方法
     * @return 返回DefaultMQPushConsumer，交给Spring去管理
     */
    @Bean(name = "CustomPushConsumer")
    public DefaultMQPushConsumer customPushConsumer() throws MQClientException {
        log.info(RocketMQConstant.ConsumerGroup.consumerGroup + "*******" + nameServer + "*******" + RocketMQConstant.Topic.topic);
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(RocketMQConstant.ConsumerGroup.consumerGroup);
        consumer.setNamesrvAddr(nameServer);
        consumer.subscribe(RocketMQConstant.Topic.topic, "*");
        // 设置每次消息拉取的时间间隔，单位毫秒
        consumer.setPullInterval(500);
        // 设置每个队列每次拉取的最大消息数
        consumer.setPullBatchSize(24);
        // 设置消费者单次批量消费的消息数目上限
        consumer.setConsumeMessageBatchMaxSize(12);
        consumer.registerMessageListener((MessageListenerConcurrently) (msgs, context)
                -> {
            List<MkOrder> orderList = new ArrayList<>(msgs.size());
            Map<Integer, Integer> queueMsgMap = new HashMap<>(8);
            msgs.forEach(msg -> {
                orderList.add(JSONObject.parseObject(msg.getBody(), MkOrder.class));
                queueMsgMap.compute(msg.getQueueId(), (key, val) -> val == null ? 1 : ++val);
            });
            log.info("MkOrderList size: {}, content: {}", orderList.size(), orderList);
            // 处理批量消息
            // orderService.orderSaveBatch(orderList);
            orderService.saveBatch(orderList);
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        });
        consumer.start();
        return consumer;
    }

    @Bean(name = "DelayPushConsumer")
    public DefaultMQPushConsumer delayPushConsumer() throws MQClientException {
        log.info(RocketMQConstant.ConsumerGroup.delayConsumerGroup + "*******" + nameServer + "*******" + RocketMQConstant.Topic.delayTopic);
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(RocketMQConstant.ConsumerGroup.delayConsumerGroup);
        consumer.setNamesrvAddr(nameServer);
        consumer.subscribe(RocketMQConstant.Topic.delayTopic, "*");
        // 设置每次消息拉取的时间间隔，单位毫秒
        consumer.setPullInterval(1000);
        // 设置每个队列每次拉取的最大消息数
        consumer.setPullBatchSize(24);
        // 设置消费者单次批量消费的消息数目上限
        consumer.setConsumeMessageBatchMaxSize(12);
        consumer.registerMessageListener((MessageListenerConcurrently) (msgs, context)
                -> {
            List<String> orderSnList = new ArrayList<>(msgs.size());
            Map<Integer, Integer> queueMsgMap = new HashMap<>(8);
            msgs.forEach(msg -> {
                orderSnList.add(JSONObject.parseObject(msg.getBody(), String.class));
                log.info("转换后的sn标识为：{}", Arrays.toString(msg.getBody()));
                queueMsgMap.compute(msg.getQueueId(), (key, val) -> val == null ? 1 : ++val);
            });
            log.info("MkDelayList size: {}, content: {}", orderSnList.size(), orderSnList);
            // 处理批量消息
            List<MkOrder> orderList = orderService.getBySnBatch(orderSnList);
            List<String> deleteIdList = new ArrayList<>();
            orderList.forEach(item -> {
                if (item.getStatus() == 0) {
                    // TODO 退还Redis库存

                    // 调用Coupon模块的减库存接口
                    // 退还数据库真实库存
                    int res = couponFeignService.refundStock(item.getProductId(), item.getSessionId());
                    if (res < 1)
                        log.info("归还库存失败，productId: {}, sessionId: {}", item.getProductId(), item.getSessionId());
                    // 放入批量删除List
                    deleteIdList.add(item.getId());
                }
            });
            orderService.removeByIds(deleteIdList);
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        });
        consumer.start();
        return consumer;
    }

//    @Bean(name = "PayPushConsumer")
//    public DefaultMQPushConsumer payPushConsumer() throws MQClientException {
//        log.info(RocketMQConstant.ConsumerGroup.payConsumerGroup + "*******" + nameServer + "*******" + RocketMQConstant.Topic.payTopic);
//        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(RocketMQConstant.ConsumerGroup.payConsumerGroup);
//        consumer.setNamesrvAddr(nameServer);
//        consumer.subscribe(RocketMQConstant.Topic.payTopic, "*");
//        // 设置每次消息拉取的时间间隔，单位毫秒
//        consumer.setPullInterval(1000);
//        // 设置每个队列每次拉取的最大消息数
//        consumer.setPullBatchSize(24);
//        // 设置消费者单次批量消费的消息数目上限
//        consumer.setConsumeMessageBatchMaxSize(12);
//        consumer.registerMessageListener((MessageListenerConcurrently) (msgs, context)
//                -> {
//            List<String> orderIdList = new ArrayList<>(msgs.size());
//            Map<Integer, Integer> queueMsgMap = new HashMap<>(8);
//            msgs.forEach(msg -> {
//                orderIdList.add(JSONObject.parseObject(msg.getBody(), String.class));
//                queueMsgMap.compute(msg.getQueueId(), (key, val) -> val == null ? 1 : ++val);
//            });
//            log.info("MkPayList size: {}, content: {}", orderIdList.size(), orderIdList);
//            // 处理批量消息
//            List<MkOrder> orderList = orderService.listByIds(orderIdList);
//            orderList.forEach(item -> orderService.payOrder(item));
//            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
//        });
//        consumer.start();
//        return consumer;
//    }
}
