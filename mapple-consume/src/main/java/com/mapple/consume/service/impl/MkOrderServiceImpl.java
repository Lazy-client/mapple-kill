package com.mapple.consume.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mapple.common.exception.RRException;
import com.mapple.common.utils.PageUtils;
import com.mapple.common.utils.Query;
import com.mapple.common.utils.RocketMQConstant;
import com.mapple.common.utils.redis.cons.RedisKeyUtils;
import com.mapple.common.utils.result.CommonResult;
import com.mapple.consume.entity.MkOrder;
import com.mapple.consume.mapper.MkOrderMapper;
import com.mapple.consume.service.AdminFeignService;
import com.mapple.consume.service.CouponFeignService;
import com.mapple.consume.service.MkOrderService;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.redisson.api.RBloomFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

    @Resource
    private RBloomFilter<String> orderBloomFilter;

    @Resource
    private RocketMQTemplate rocketMQTemplate;

    @Resource
    private CouponFeignService couponFeignService;

    @Resource
    private AdminFeignService adminFeignService;

    @Resource
    private ValueOperations<String, String> valueOperations;


    /**
     * 传入一个订单之后，消息生产者将order封装成信息，进入消息队列，进行流量削峰
     * 消息接收方进行实时的监听，接收到消息后，将消息解码变为对象，然后
     * 调用相应的接口的方法，落盘到数据库
     * 消息接收方
     */
    // Transaction
    @Override
    public CommonResult orderEnqueue(MkOrder order) {
        log.info("获取到topic: {}, 获取到tag: {}", RocketMQConstant.Topic.topic, RocketMQConstant.Tag.tag);
        try {
//            orderSendTransaction2(order);
//            orderSendOneWay(RocketMQConstant.Topic.topic, order);
            rocketMQTemplate.syncSend(RocketMQConstant.Topic.topic,
                    MessageBuilder.withPayload(order).build(),
                    10000,
                    2);
        } catch (Exception e) {
            e.printStackTrace();
            return CommonResult.error("消息发送失败");
        }
        return CommonResult.ok();
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params, String userId) {


        boolean statusFlag = params.get("status") != null;

        IPage<MkOrder> page = this.page(
                new Query<MkOrder>().getPage(params),
                new QueryWrapper<MkOrder>()
                        .eq(StringUtils.isNotBlank(userId), "user_id", userId)
                        // 0-未支付状态, 1-已支付
                        .eq(statusFlag, "status", params.get("status"))
        );

        return new PageUtils(page);
    }


    @Override
    @GlobalTransactional(timeoutMills = 50000, name = "Consume-PayOrder")    // Seata分布式事务
    public CommonResult payOrder(MkOrder order) {
        log.info("开始全局事务......");
        // 减库存
        String productId = order.getProductId();
        String sessionId = order.getSessionId();
        // 调用Coupon模块的减库存接口
        int result = couponFeignService.deductStock(productId, sessionId);
        if (result < 0) {
            log.info("result==={}", result);
            throw new RRException("扣减库存失败");
        }
        // 减本账户余额
        String userId = order.getUserId();
        BigDecimal payAmount = order.getPayAmount();
        // 调用admin模块的接口
        log.info("进入远程调用，减余额");
        R r = adminFeignService.deductBalance(userId, payAmount);
        log.info("余额调用结束，结果{}", r.getMsg());
        long code = r.getCode();
        String msg = r.getMsg();
        // 给Redis公共账户加钱
        if (code == 0) {
            log.info("PayAmount转换的值: {}", payAmount.longValue());
            Long increment = valueOperations.increment(RedisKeyUtils.PUBLIC_ACCOUNT, payAmount.longValue());
            if (increment == null || increment <= 0)
                throw new RRException("新增Redis公共账户余额失败");
            else {
                // 设置订单状态为已支付
                order.setStatus(1);
                boolean flag = this.updateById(order);
                if (!flag)
                    throw new RRException("更新订单状态失败");
                // 支付成功,加入订单id到orderBloomFilter
                orderBloomFilter.add(order.getId());
                return CommonResult.ok("执行成功");
            }
        }
        throw new RRException(msg);
    }

//    @Override
//    @GlobalTransactional(timeoutMills = 30000, name = "Consume-PayOrder")    // Seata分布式事务
//    public CommonResult payOrder(MkOrder order) {
//        log.info("开始全局事务......");
//        // 减库存
//        String productId = order.getProductId();
//        String sessionId = order.getSessionId();
//        Integer productCount = order.getProductCount();
//        // 调用Coupon模块的减库存接口
//        int result = couponFeignService.deductStock(productId, sessionId);
//        if (result < 1) {
//            log.info("result==={}", result);
//            throw new RRException("扣减库存失败");
//        }
//        // 减本账户余额
//        String userId = order.getUserId();
//        BigDecimal payAmount = order.getPayAmount();
//        // 调用admin模块的接口
//        log.info("进入远程调用，减余额");
//        R r = adminFeignService.deductBalance(userId, payAmount);
//        log.info("余额调用结束，结果{}", r.getMsg());
//        long code = r.getCode();
//        String msg = r.getMsg();
//        // 给Redis公共账户加钱
//        if (code == 0) {
//            log.info("PayAmount转换的值: {}", payAmount.longValue());
//            Long increment = valueOperations.increment(RedisKeyUtils.PUBLIC_ACCOUNT, payAmount.longValue());
//            if (increment == null || increment <= 0)
//                throw new RRException("扣减公共账户余额失败");
//            else {
//                // 设置订单状态为已支付
//                order.setStatus(1);
//                boolean flag = this.updateById(order);
//                if (!flag)
//                    throw new RRException("更新订单状态失败");
//                return CommonResult.ok("执行成功");
//            }
//        }
//        throw new RRException(msg);
//    }

    @Override
    public CommonResult publicAccountBalance() {
        String balance = valueOperations.get(RedisKeyUtils.PUBLIC_ACCOUNT);
        return CommonResult.ok().put("balance", balance);
    }

    @Override
    public List<String> getTimeoutRandomCodeList(long timeout, long currentTime) {
        List<String> randomCodeList = new ArrayList<>();
        List<String> deleteIdList = new ArrayList<>();
        this.list(new QueryWrapper<MkOrder>()
                .eq("status", 0))
                .forEach(item -> {
                    if ((currentTime - item.getGmtCreate().getTime()) > timeout) {
                        randomCodeList.add(item.getRandomCode());
                        // 删除该条记录
                        deleteIdList.add(item.getId());
                    }
                });
        // 批量删除
        this.removeByIds(deleteIdList);
        return randomCodeList;
    }

    @Override
    public List<MkOrder> getBySnBatch(List<String> orderSnList) {
        return baseMapper.getBySnBatch(orderSnList);
    }


    @Override
    public CommonResult sendDelay(String orderSn) {
        log.info("发送的sn标识为： {}", orderSn);
        rocketMQTemplate.syncSend(RocketMQConstant.Topic.delayTopic, MessageBuilder.withPayload(orderSn).build(), 10000);
//        rocketMQTemplate.syncSend(messageDelayTopic, MessageBuilder.withPayload(orderSn).build(), 10000, 0);
        return CommonResult.ok();
    }

    @Override
    public CommonResult payOrderEnqueue(String orderId) {
        MkOrder order = this.getById(orderId);
        if (order == null)
            return CommonResult.error("订单不存在!");
        if (order.getStatus() == 1)
            return CommonResult.error("订单已支付，请勿重复操作！");
        rocketMQTemplate.asyncSend(RocketMQConstant.Topic.delayTopic, MessageBuilder.withPayload(orderId).build(), new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                log.info("支付成功，订单id为:{}", orderId);
            }

            @Override
            public void onException(Throwable throwable) {
                log.info("支付失败，订单id为:{}, 错误消息为: {}", orderId, throwable.getMessage());
            }
        }, 10000);
        return CommonResult.ok("支付请求成功，如支付后订单状态仍未更新，请充值个人账户");
    }


    @Override
    @GlobalTransactional
    public void orderSaveBatch(List<MkOrder> orderList) {
        this.saveBatch(orderList);
        // 进行真实库存扣减
        orderList.forEach(item -> {
            // 调用Coupon模块的减库存接口
            int res = couponFeignService.deductStock(item.getProductId(), item.getSessionId());
            if (res < 0)
                log.info("扣减库存失败，productId: {}, sessionId: {}", item.getProductId(), item.getSessionId());
        });
    }


    // SendOneWay
    //    @Override
    //    public CommonResult orderEnqueue(MkOrder order) {
    //        log.info("获取到topic: {}, 获取到tag: {}", topic, tag);
    //        try {
    //            // 此处应该使用fastJSON自带的toString方法，将对象字符串化
    //             orderSendOneWay(topic, order);
    //        } catch (Exception e) {
    //            e.printStackTrace();
    //            return CommonResult.error("消息发送失败");
    //        }
    //        return CommonResult.ok();
    //    }

    // Common Version
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


    // Common Version
//    private void sendOrder(String topic, String tag, String keys, String body) throws InterruptedException, RemotingException, MQClientException, MQBrokerException {
//        Message message = new Message(topic, tag, keys, body.getBytes());
//        // "1s 5s 10s 30s 1m 2m 3m 4m 5m 6m 7m 8m 9m 10m 20m 30m 1h 2h"
//        // 设置消息延迟等级,延迟5秒发送
//        message.setDelayTimeLevel(2);
//        rocketMQTemplate.getProducer().send(message);
//    }

    // SendOneWay Version
    private void orderSendOneWay(String topic, MkOrder order) {
        // 异步发送，不需要Broker的确认回复，性能更高，但是存在消息丢失的可能
        rocketMQTemplate.sendOneWay(topic, MessageBuilder.withPayload(order).build());
    }

    /*
     * 发送事务消息
     * Transaction Version
     */
//    private void orderSendTransaction(MkOrder order) {
//        String transactionId = UUID.randomUUID().toString();
//        // 设置事务id
//        order.setTransactionId(transactionId);
//        // 使用模板发送事务消息
//        rocketMQTemplate.createAndStartTransactionMQProducer(
//                transactionProducerGroup,
//                new MkOrderTransactionListener(),
//                null,
//                null);
//        // 发送事务消息
//        String keys = order.getUserId().concat(order.getProductId()).concat(order.getSessionId());
//        TransactionSendResult result =
//                rocketMQTemplate.sendMessageInTransaction(
//                        transactionProducerGroup,
//                        transactionTopic,
//                        MessageBuilder.withPayload(order).build(),
//                        tag);
//        log.info("【事务消息发送状态】：{}", result.getLocalTransactionState());
//    }
//
//    private void orderSendTransaction2(MkOrder order) throws MQClientException {
//        String transactionId = UUID.randomUUID().toString();
//        // 设置事务id
//        order.setTransactionId(transactionId);
//        // 使用模板发送事务消息
//        TransactionMQProducer producer = new TransactionMQProducer();
//        producer.setNamesrvAddr(nameServer);
//        producer.setProducerGroup(transactionProducerGroup);
//        producer.setTransactionListener(new MkOrderTransactionListener2());
//        producer.start();
//        // 发送事务消息
//        String keys = order.getUserId().concat(order.getProductId()).concat(order.getSessionId());
//        Message message = new Message(transactionTopic, tag, keys, JSON.toJSONString(order).getBytes());
//        TransactionSendResult result = producer.sendMessageInTransaction(message, null);
//        log.info("【事务消息发送状态】：{}", result.getLocalTransactionState());
//
//    }
}
