package com.mapple.consume;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.mapple.common.vo.MkOrder;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.support.MessageBuilder;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.UUID;

/**
 * @author : Gelcon
 * @date : 2022/4/4 14:45
 */
@SpringBootTest
public class OrderSendTest {

    @Resource
    private RocketMQTemplate rocketMQTemplate;


    @Test
    public void sendOrder() {
        MkOrder order = new MkOrder();
        order.setOrderSn(UUID.randomUUID().toString());
        rocketMQTemplate.send("MkOrder-Topic", MessageBuilder.withPayload(order).build());
    }

    @Test
    public void testSendDelayOrder() {
        //生成订单发消息 设置订单属性
        MkOrder order = new MkOrder();
        order.setUserId("1505814885762260994");
        order.setSessionId("1504477072970100738");
        order.setRandomCode("123456");
        order.setProductId("1504478478212935682");
        order.setOrderSn(IdWorker.get32UUID());
        // 写死，默认只能抢购1份
        order.setProductCount(1);
        // 暂时先写成一样的
        order.setTotalAmount(BigDecimal.valueOf(10000));
        order.setPayAmount(BigDecimal.valueOf(100));
        // 1天内不支付，自动取消
        order.setAutoConfirmDay(1);
        // 未支付状态
        order.setStatus(0);
        if (order.getOrderSn() == null)
            System.out.println("空值");
        rocketMQTemplate.syncSend("MkOrder-Delay-Topic", MessageBuilder.withPayload(order.getOrderSn()).build(), 10000, 15);
    }
}
