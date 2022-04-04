package com.mapple.seckill;

import com.mapple.common.vo.MkOrder;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.support.MessageBuilder;

import javax.annotation.Resource;

/**
 * @author : Gelcon
 * @date : 2022/4/4 14:45
 */
@SpringBootTest
public class OrderSendTest {

    @Resource
    private RocketMQTemplate rocketMQTemplate;

    @Value("${mq.order.topic}")
    private String messageTopic;

    @Test
    public void sendOrder() {
        MkOrder order = new MkOrder();
        order.setUserId("1505814885762260993");
        order.setSessionId("1504477072970100738");
        order.setProductId("1507031366130905090");
        if (messageTopic == null)
            System.out.println("messageTopic为null");
        rocketMQTemplate.send(messageTopic, MessageBuilder.withPayload(order).build());
    }
}
