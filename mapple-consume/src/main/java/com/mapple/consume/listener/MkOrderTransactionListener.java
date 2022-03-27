package com.mapple.consume.listener;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mapple.consume.entity.MkOrder;
import com.mapple.consume.service.MkOrderService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionState;
import org.apache.rocketmq.spring.support.RocketMQHeaders;
import org.springframework.messaging.Message;

import javax.annotation.Resource;

/**
 * @author Gelcon
 * @since 2022-03-24
 */
//@RocketMQTransactionListener(txProducerGroup = "${mq.order.producer.group}")
//@Component
@Slf4j
public class MkOrderTransactionListener implements RocketMQLocalTransactionListener {

    @Resource
    private MkOrderService orderService;

    /**
     * 执行本地事务，在第一步中消息发送成功会回调执行，
     * 一旦事务提交成功，下游应用的Consumer能收到该消息
     */
    @Override
    public RocketMQLocalTransactionState executeLocalTransaction(Message message, Object o) {
        try {
            // 消息转换
            MkOrder order = (MkOrder) message.getPayload();
            // 执行本地事务
            // 获取前面生成的事务ID
            String transactionId = order.getTransactionId();
            log.info("生成的事务id: {}", transactionId);
            log.info("测试，转换的订单对象：{}", order.toString());
            boolean flag = orderService.save(order);
            log.info("【本地业务执行完毕】 msg:{}, Object:{}", message, o);
            return flag ? RocketMQLocalTransactionState.COMMIT : RocketMQLocalTransactionState.ROLLBACK;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("【执行本地业务异常】 exception message:{}", e.getMessage());
            return RocketMQLocalTransactionState.ROLLBACK;
        }
    }

    /**
     * 检查本地事务执行状态，
     * 如果executeLocalTransaction方法中返回的状态是
     * 未知UNKNOWN或者未返回状态，
     * 默认会在预处理发送的1分钟后由Broker通知Producer检查本地事务，
     * 在Producer中回调本地事务监听器中的checkLocalTransaction方法。
     * 检查本地事务时，可以根据事务ID查询本地事务的状态，
     * 再返回具体事务状态给Broker。
     */
    @Override
    public RocketMQLocalTransactionState checkLocalTransaction(Message message) {
        log.info("【执行检查任务】");
        // 获取前面生成的事务ID
        String transactionId = (String) message.getHeaders().get(RocketMQHeaders.TRANSACTION_ID);
        if (isSuccess(transactionId)) {
            return RocketMQLocalTransactionState.COMMIT;
        }
        return RocketMQLocalTransactionState.UNKNOWN;
    }

    private boolean isSuccess(String transactionId) {
        // 查询数据库 select from 订单表
        return orderService.getOne(
                new QueryWrapper<MkOrder>()
                        .eq("transaction_id", transactionId)) != null;
    }
}

