package com.mapple.seckill.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.mapple.common.utils.CryptogramUtil;
import com.mapple.common.utils.RocketMQConstant;
import com.mapple.common.utils.jwt.JwtUtils;
import com.mapple.common.utils.redis.cons.RedisKeyUtils;
import com.mapple.common.utils.result.CommonResult;
import com.mapple.common.vo.MkOrder;
import com.mapple.common.vo.Session;
import com.mapple.common.vo.Sku;
import com.mapple.seckill.service.SecKillService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RMapCache;
import org.redisson.api.RSemaphore;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author zsc
 * @version 1.0
 * @date 2022/2/17 18:00
 */
@Service
@Slf4j
public class SeckillServiceImpl implements SecKillService {


    protected Logger logger = LoggerFactory.getLogger(getClass());
    @Resource
    RedissonClient redissonClient;
    @Resource
    private HashOperations<String, String, String> hashOperations;

    @Resource
    private RocketMQTemplate rocketMQTemplate;

//    @Value("${mq.order.topic}")
//    private String messageTopic;
//
//    @Value("${mq.order.delayTopic}")
//    private String messageDelayTopic;

    @Override
    public String kill(String key, String id, String token) throws InterruptedException {
        String userId = getUserId(token);
        if (!StringUtils.isEmpty(userId)) {
            logger.info(userId);
            //id ====> sessionId-productId
            long currentTime = System.currentTimeMillis();
            //校验场次的sku是否存在
            String skuJason = hashOperations.get(RedisKeyUtils.SKU_PREFIX, id);
            if (!StringUtils.isEmpty(skuJason)) {
                Sku sku = JSON.parseObject(skuJason, Sku.class);
                if (currentTime >= sku.getStartTime().getTime() && currentTime < sku.getEndTime().getTime()) {//校验时间
                    logger.info("时间校验通过");
                    //校验用户是有参与过秒杀
                    if (!hashOperations.hasKey(RedisKeyUtils.SECKILL_USER_PREFIX, userId + "-" + key)) {
                        logger.info("用户{}未参与过秒杀活动", userId);
                        //分布式锁减库存
                        RSemaphore semaphore = redissonClient.getSemaphore(RedisKeyUtils.STOCK_PREFIX + key);
                        //  生产环境调为10ms内未减掉库存,那么库存就空了
                        boolean acquire = semaphore.tryAcquire(1, 10, TimeUnit.MILLISECONDS);
                        if (acquire) {//库存与扣成功
                            logger.info("用户{}----秒杀成功", userId);
                            RMapCache<Object, Object> userMap = redissonClient.getMapCache(RedisKeyUtils.SECKILL_USER_PREFIX);
                            userMap.put(userId + "-" + key, "1", 6, TimeUnit.HOURS);
                            //生成订单发消息 设置订单属性
                            MkOrder order = new MkOrder();
                            order.setUserId(userId);
                            order.setSessionId(sku.getId());
                            order.setRandomCode(sku.getRandomCode());
                            order.setSessionName(sku.getSessionName());
                            order.setProductId(sku.getProductId());
                            order.setProductName(sku.getProductName());
                            order.setStatus(0);
                            order.setOrderSn(IdWorker.get32UUID());
                            // 写死，默认只能抢购1份
                            order.setProductCount(1);
                            // 暂时先写成一样的
                            order.setTotalAmount(sku.getSeckillPrice());
                            order.setPayAmount(sku.getSeckillPrice());
                            // 1天内不支付，自动取消
                            order.setAutoConfirmDay(1);
                            // 未支付状态
                            order.setStatus(0);
                            // 创建订单消息
                            rocketMQTemplate.syncSend(RocketMQConstant.Topic.topic, MessageBuilder.withPayload(order).build(), 10000, 2);
                            // 延时队列消息，20分钟后去监听是否支付
                            // 延时等级，1到18
                            // 1s 5s 10s 30s 1m 2m 3m 4m 5m 6m 7m 8m 9m 10m 20m 30m 1h 2h
                            // 超时时间10s, 延时时间20min
//                            rocketMQTemplate.syncSend(RocketMQConstant.Topic.delayTopic, MessageBuilder.withPayload(order.getOrderSn()).build(), 10000, 15);
                            return "ok";
                        }
                    }
                }
            }
        }
        return null;
    }

    private String getUserId(String token) {
        String jwt = CryptogramUtil.doDecrypt(token);
        return JwtUtils.getUserId(jwt);
    }

    @Override
    public List<Sku> search(String sessionId) {
        long time = System.currentTimeMillis();
        //开始时间-结束时间
        String json = hashOperations.get(RedisKeyUtils.SESSIONS_PREFIX, sessionId);
        if (StringUtils.isEmpty(json)) {
            return null;
        }
        //关联的产品
        String products = hashOperations.get(RedisKeyUtils.SKUS_PREFIX, sessionId);
        if (StringUtils.isEmpty(products)) {
            //查询所有的sku
            return null;
        }

        String[] times = json.split("-");
        List<Sku> skus = JSON.parseArray(products, Sku.class);
        //秒杀期间的场次
        logger.info("products ---->{}", products);
        logger.info("skus--{}", skus);
        if (time >= Long.parseLong(times[0]) && time < Long.parseLong(times[1])) {
            return skus;
        }
        //掩盖随机码
        else if (time < Long.parseLong(times[0])) {
            logger.info("进入掩盖随机码");
            assert skus != null;
            skus.forEach(sku -> {
                sku.setRandomCode(null);
                sku.setId(sessionId);
            });
        }

        return skus;
    }

    @Resource
    RBloomFilter<String> userBloomFilter;

    @Override
    public Map<String, List<Session>> searchSessions(String token) {
        long currentTime = System.currentTimeMillis();
        logger.info("token=====>{}", token);
        String userId = getUserId(token);
        logger.info("userId===={}", userId);
        boolean passed = true;
        if (userBloomFilter.isExists()) {
            logger.info("开启初筛");
            passed = userBloomFilter.contains(userId);
            logger.info("布隆过滤器是否有userId===={}", userBloomFilter.contains(userId));
        }
        Map<String, String> sessions = hashOperations.entries(RedisKeyUtils.SESSIONS_PREFIX);
        Set<String> sessionIds = sessions.keySet();
        List<Session> sessionList = new ArrayList<>();
        List<Session> sessionListNotStart = new ArrayList<>();


        logger.info("go to sessionSearch===number{}", sessionIds.size());
        boolean finalPassed = passed;
        sessionIds.forEach(
                sessionId -> {
                    String times = sessions.get(sessionId);
                    String[] sToEnd = times.split("-");
                    Session session = new Session();
                    // 未通过初筛,不设置sessionId,后续数据用户也看不到了
                    if (finalPassed) {
                        logger.info("user is pass:{}", finalPassed);
                        session.setSessionId(sessionId);
                    }
                    //设置 session-name
                    session.setStartTime(Long.parseLong(sToEnd[0]));
                    session.setEndTime(Long.parseLong(sToEnd[1]));
                    session.setSessionName(sToEnd[2]);
                    //当前秒杀的场次
                    if (currentTime >= session.getStartTime() && currentTime < session.getEndTime()) {
                        //如果初筛未通过 只能看到sessionName 和时间,id也不能返回
                        if (finalPassed) {
                            String skus = hashOperations.get(RedisKeyUtils.SKUS_PREFIX, sessionId);
                            session.setSkus(JSON.parseArray(skus));
                        }
                        sessionList.add(session);
                    } else if (currentTime < session.getStartTime()) {
                        sessionListNotStart.add(session);
                    }
                }
        );
        Map<String, List<Session>> map = new HashMap<>();
        map.put("ing", sessionList);
        map.put("notStart", sessionListNotStart);
        return map;
    }

    @Override
    public JSON searchById(String sessionId, String productId) {
        long currentTime = System.currentTimeMillis();
        JSONObject jsonObject = JSON.parseObject(hashOperations.get(RedisKeyUtils.SKU_PREFIX, sessionId + "-" + productId));
        if (jsonObject == null)
            return null;

        // 未开始,掩盖随机码
        if (currentTime < jsonObject.getLongValue("startTime")) {
            jsonObject.remove("randomCode");
        }
        return jsonObject;
    }

    @Override
    public CommonResult sendOrder() {
        MkOrder order = new MkOrder();
        order.setUserId("1505814885762260993");
        order.setSessionId("1504477072970100738");
        order.setProductId("1507031366130905090");
        rocketMQTemplate.send(RocketMQConstant.Topic.topic, MessageBuilder.withPayload(order).build());
        log.info("测试发送消息：主题为：{}", RocketMQConstant.Topic.topic);
        return CommonResult.ok();
    }
}
