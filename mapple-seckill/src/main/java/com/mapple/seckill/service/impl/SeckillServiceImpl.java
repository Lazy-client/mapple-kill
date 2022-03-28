package com.mapple.seckill.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.mapple.common.utils.CryptogramUtil;
import com.mapple.common.utils.jwt.JwtUtils;
import com.mapple.common.utils.redis.cons.RedisKeyUtils;
import com.mapple.common.vo.MkOrder;
import com.mapple.common.vo.Session;
import com.mapple.common.vo.Sku;
import com.mapple.seckill.service.SecKillService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.redisson.api.RSemaphore;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    @Override
    public String kill(String key, String id, String token) throws InterruptedException {
        String jwt = CryptogramUtil.doDecrypt(token);
        String userId = JwtUtils.getUserId(jwt);
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
                        boolean acquire = semaphore.tryAcquire(1, 100, TimeUnit.MILLISECONDS);
                        if (acquire) {//库存与扣成功
                            logger.info("用户{}----秒杀成功", userId);
                            hashOperations.put(RedisKeyUtils.SECKILL_USER_PREFIX, userId + "-" + key, "1");
                            //TODO 生成订单发消息
                            MkOrder order = new MkOrder();
                            // 拆解
                            String[] split = id.split("-");
                            order.setUserId(userId);
                            order.setSessionId(split[0]);
                            order.setProductId(split[1]);
                            order.setOrderSn(IdWorker.get32UUID());

                            rocketMQTemplate.asyncSend("Mk-Topic", MessageBuilder.withPayload(order).build(), new SendCallback() {
                                @Override
                                public void onSuccess(SendResult sendResult) {
                                    log.info("发送成功: {}", sendResult.getSendStatus());
                                }

                                @Override
                                public void onException(Throwable throwable) {
                                    log.info("发送失败: {}", throwable.getMessage());
                                }
                            });
                            return "ok";
                        }
                    }
                }
            }
        }
        return null;
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

    @Override
    public Map<String, List<Session>> searchSessions() {
        long currentTime = System.currentTimeMillis();
        Map<String, String> sessions = hashOperations.entries(RedisKeyUtils.SESSIONS_PREFIX);
        Set<String> sessionIds = sessions.keySet();
        List<Session> sessionList = new ArrayList<>();
        List<Session> sessionListNotStart = new ArrayList<>();
        sessionIds.forEach(
                sessionId -> {
                    String times = sessions.get(sessionId);
                    String[] sToEnd = times.split("-");
                    Session session = new Session();
                    session.setSessionId(sessionId);
                    //设置 session-name
                    session.setStartTime(Long.parseLong(sToEnd[0]));
                    session.setEndTime(Long.parseLong(sToEnd[1]));
                    session.setSessionName(sToEnd[2]);
                    //当前秒杀的场次
                    if (currentTime >= Long.parseLong(sToEnd[0]) && currentTime < Long.parseLong(sToEnd[1])) {
                        String skus = hashOperations.get(RedisKeyUtils.SKUS_PREFIX, sessionId);
                        session.setSkus(JSON.parseArray(skus));
                        sessionList.add(session);
                    } else if (currentTime < Long.parseLong(sToEnd[0])) {
                        sessionListNotStart.add(session);
                    }
                }
        );
        Map<String, List<Session>> map = new HashMap<>();
        map.put("ing", sessionList);
        map.put("notStart", sessionListNotStart);
        return map;
    }
}
