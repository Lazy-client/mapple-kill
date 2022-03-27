package com.mapple.consume.listener;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mapple.consume.entity.MkOrder;
import com.mapple.consume.service.MkOrderService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.LocalTransactionState;
import org.apache.rocketmq.client.producer.TransactionListener;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionState;
import org.apache.rocketmq.spring.support.RocketMQHeaders;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;

/**
 * @author : Gelcon
 * @date : 2022/3/27 17:24
 */
@Slf4j
public class MkOrderTransactionListener2 implements TransactionListener {

    @Resource
    private MkOrderService orderService;

    @Override
    public LocalTransactionState executeLocalTransaction(Message message, Object o) {
        try {
            // 消息转换
            String body = new String(message.getBody(), StandardCharsets.UTF_8);
            MkOrder order = JSON.parseObject(body, MkOrder.class);
            // 执行本地事务
            // 获取前面生成的事务ID
            String transactionId = order.getTransactionId();
            log.info("生成的事务id: {}", transactionId);
            log.info("生成的订单实体类: {}", order.toString());
            boolean flag = orderService.save(order);
            log.info("【本地业务执行完毕】 msg:{}, Object:{}", message, o);
            return flag ? LocalTransactionState.COMMIT_MESSAGE : LocalTransactionState.ROLLBACK_MESSAGE;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("【执行本地业务异常】 exception message:{}", e.getMessage());
            return LocalTransactionState.ROLLBACK_MESSAGE;
        }
    }

    @Override
    public LocalTransactionState checkLocalTransaction(MessageExt messageExt) {
        log.info("【执行检查任务】");
        // 获取前面生成的事务ID
        String body = new String(messageExt.getBody(), StandardCharsets.UTF_8);
        MkOrder order = JSON.parseObject(body, MkOrder.class);
        if (isSuccess(order.getTransactionId())) {
            return LocalTransactionState.COMMIT_MESSAGE;
        }
        return LocalTransactionState.UNKNOW;
    }

    private boolean isSuccess(String transactionId) {
        // 查询数据库 select from 订单表
        return orderService.getOne(
                new QueryWrapper<MkOrder>()
                        .eq("transaction_id", transactionId)) != null;
    }
}
