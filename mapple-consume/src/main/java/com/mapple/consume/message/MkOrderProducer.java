package com.mapple.consume.message;

import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @desc 秒杀订单生产者初始化
 */
@Component
public class MkOrderProducer {

    private static final Logger LOGGER = LoggerFactory.getLogger(MkOrderProducer.class);

    @Autowired
    MQNamesrvConfig namesrvConfig;


    private DefaultMQProducer defaultMQProducer;

    @PostConstruct
    public void init() {
        defaultMQProducer =
                new DefaultMQProducer
                        (MessageProtocolConst.MK_ORDER_TOPIC.getProducerGroup());
        defaultMQProducer.setNamesrvAddr(namesrvConfig.nameSrvAddr());
        // 发送失败重试次数
        defaultMQProducer.setRetryTimesWhenSendFailed(3);
        try {
            defaultMQProducer.start();
        } catch (MQClientException e) {
            LOGGER.error("[秒杀订单生产者]--MkOrderProducer加载异常!e={}", LogExceptionWapper.getStackTrace(e));
            throw new RuntimeException("[秒杀订单生产者]--MkOrderProducer加载异常!", e);
        }
        LOGGER.info("[秒杀订单生产者]--MkOrderProducer加载完成!");
    }

    public DefaultMQProducer getProducer() {
        return defaultMQProducer;
    }
}
