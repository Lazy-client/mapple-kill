package com.mapple.consume.service.impl;

import com.alibaba.fastjson.JSON;
import com.mapple.common.utils.CommonResult;
import com.mapple.consume.entity.MkOrder;
import com.mapple.consume.mapper.MkOrderMapper;
import com.mapple.consume.service.MkOrderService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 订单信息表 服务实现类
 * </p>
 *
 * @author Gelcon
 * @since 2022-03-19
 */
@Service
@Slf4j
public class MkOrderServiceImpl extends ServiceImpl<MkOrderMapper, MkOrder> implements MkOrderService {

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    @Value("${mq.order.topic}")
    private String topic;

    @Value("${mq.order.tag}")
    private String tag;

    /**
     * 传入一个订单之后，消息生产者将order封装成信息，进入消息队列，进行流量削峰
     * 消息接收方进行实时的监听，接收到消息后，将消息解码变为对象，然后
     * 调用相应的接口的方法，落盘到数据库
     * 消息接收方
     */
    @Override
    public CommonResult orderEnqueue(MkOrder order) {

        log.info("获取到topic: {}, 获取到tag: {}", topic, tag);
//        String key = order.getUserId().concat(order.getProductId()).concat(order.getSessionId());
        try {
            // 此处应该使用fastJSON自带的toString方法，将对象字符串化
            orderSendOneWay(topic, order);
        } catch (Exception e) {
            e.printStackTrace();
            return CommonResult.error("消息发送失败");
        }
        return CommonResult.ok();
    }
    //    @Override
    //    public CommonResult orderEnqueue(MkOrder order) {
    //
    //        log.info("获取到topic: {}, 获取到tag: {}", topic, tag);
    //        String key = order.getUserId().concat(order.getProductId()).concat(order.getSessionId());
    //        try {
    //            // 此处应该使用fastJSON自带的toString方法，将对象字符串化
    //            sendOrder(topic, tag, key, JSON.toJSONString(order));
    //        } catch (InterruptedException | RemotingException | MQClientException | MQBrokerException e) {
    //            e.printStackTrace();
    //            return CommonResult.error("消息发送失败");
    //        }
    //        return CommonResult.ok();
    //    }

    /**
     * 发送订单消息
     *
     * @param topic 主题
     * @param tag   标签
     * @param keys  标识
     * @param body  消息体
     */
    private void sendOrder(String topic, String tag, String keys, String body) throws InterruptedException, RemotingException, MQClientException, MQBrokerException {
        Message message = new Message(topic, tag, keys, body.getBytes());
        // "1s 5s 10s 30s 1m 2m 3m 4m 5m 6m 7m 8m 9m 10m 20m 30m 1h 2h"
        // 设置消息延迟等级,延迟5秒发送
        message.setDelayTimeLevel(2);
         rocketMQTemplate.getProducer().send(message);
    }


    private void orderSendOneWay(String topic, MkOrder order) {
        // 异步发送，不需要Broker的确认回复，性能更高，但是存在消息丢失的可能
        rocketMQTemplate.sendOneWay(topic, MessageBuilder.withPayload(order).build());
    }
}
