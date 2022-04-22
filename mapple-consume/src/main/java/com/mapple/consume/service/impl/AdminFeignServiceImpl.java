package com.mapple.consume.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mapple.common.utils.Lua;
import com.mapple.common.utils.RocketMQConstant;
import com.mapple.common.utils.redis.cons.RedisKeyUtils;
import com.mapple.common.utils.result.CommonResult;
import com.mapple.common.vo.MkOrderPay;
import com.mapple.consume.entity.UserEntity;
import com.mapple.consume.mapper.UserDao;
import com.mapple.consume.service.AdminFeignService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RScript;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @author hxx
 * @date 2022/4/21 11:06
 */
@Service
@Slf4j
public class AdminFeignServiceImpl extends ServiceImpl<UserDao, UserEntity> implements AdminFeignService {

    //    @Resource
    //    private RBloomFilter<String> payBloomFilter;
    @Resource
    private RBloomFilter<String> orderBloomFilter;

    @Resource
    RedissonClient redissonClient;

    @Autowired
    public RedisTemplate<String, String> stringRedisTemplate;

    @Resource
    private RocketMQTemplate rocketMQTemplate;

    //绑定userBalance的hash
    BoundHashOperations<String, String, String> operationsForBalance;

    @Autowired
    public AdminFeignServiceImpl(RedisTemplate<String, String> stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
        operationsForBalance = stringRedisTemplate.boundHashOps(RedisKeyUtils.USER_BALANCE);
    }

    @Override
    public CommonResult deductBalance(MkOrderPay pay) {
        String balance = Lua.banlance.getLua();
        RScript script = redissonClient.getScript();
        List<Object> keys = new ArrayList<>();
        keys.add(RedisKeyUtils.USER_BALANCE);
        keys.add(pay.getUserId());
        boolean re = script.eval(
                RScript.Mode.READ_WRITE,
                balance,
                RScript.ReturnType.BOOLEAN,
                keys,
                pay.getPayAmount().toString());
        if (re) {
            //放入订单布隆过滤器，加的是订单SN标识
            orderBloomFilter.add(pay.getOrderSn());
            // 发支付消息
            Message message = new Message();
            message.setTopic(RocketMQConstant.Topic.payTopic);
            // 延时等级，1到18
            // 1s 5s 10s 30s 1m 2m 3m 4m 5m 6m 7m 8m 9m 10m 20m 30m 1h 2h
            // message.setDelayTimeLevel(1);
            // 进行幂等性处理
            message.setKeys(pay.getOrderSn());
            // 设置消息体
            message.setBody(pay.toString().getBytes());
            try {
                rocketMQTemplate.getProducer().send(message);
                log.info("userId为{}的用户支付orderId为{}的订单成功", pay.getUserId(), pay.getId());
                return CommonResult.ok("支付成功");
            } catch (MQClientException | RemotingException | MQBrokerException | InterruptedException e) {
                e.printStackTrace();
                log.info("userId为{}的用户支付orderId为{}的订单失败", pay.getUserId(), pay.getId());
            }
            return CommonResult.ok("支付失败");
        }
        return CommonResult.error("支付失败");
    }

    @Override
    public int deductMoney(BigDecimal payAmount, String userId, long version) {
        return baseMapper.deductMoney(payAmount, userId, version);
    }
}
