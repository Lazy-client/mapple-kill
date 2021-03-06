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
            //???????????????sku????????????
            String skuJason = hashOperations.get(RedisKeyUtils.SKU_PREFIX, id);
            if (!StringUtils.isEmpty(skuJason)) {
                Sku sku = JSON.parseObject(skuJason, Sku.class);
                if (currentTime >= sku.getStartTime().getTime() && currentTime < sku.getEndTime().getTime()) {//????????????
                    logger.info("??????????????????");
                    //?????????????????????????????????
                    if (!hashOperations.hasKey(RedisKeyUtils.SECKILL_USER_PREFIX, userId + "-" + key)) {
                        logger.info("??????{}????????????????????????", userId);
                        //?????????????????????
                        RSemaphore semaphore = redissonClient.getSemaphore(RedisKeyUtils.STOCK_PREFIX + key);
                        //  ??????????????????10ms??????????????????,?????????????????????
                        boolean acquire = semaphore.tryAcquire(1, 10, TimeUnit.MILLISECONDS);
                        if (acquire) {//??????????????????
                            logger.info("??????{}----????????????", userId);
                            RMapCache<Object, Object> userMap = redissonClient.getMapCache(RedisKeyUtils.SECKILL_USER_PREFIX);
                            userMap.put(userId + "-" + key, "1", sku.getEndTime().getTime() - sku.getStartTime().getTime(), TimeUnit.MILLISECONDS);
                            //????????????????????? ??????????????????
                            MkOrder order = new MkOrder();
                            order.setUserId(userId);
                            order.setSessionId(sku.getId());
                            order.setRandomCode(sku.getRandomCode());
                            order.setSessionName(sku.getSessionName());
                            order.setProductId(sku.getProductId());
                            order.setProductName(sku.getProductName());
                            order.setStatus(0);
                            order.setOrderSn(IdWorker.get32UUID());
                            // ???????????????????????????1???
                            order.setProductCount(1);
                            // ????????????????????????
                            order.setTotalAmount(sku.getSeckillPrice());
                            order.setPayAmount(sku.getSeckillPrice());
                            // 1??????????????????????????????
                            order.setAutoConfirmDay(1);
                            // ???????????????
                            order.setStatus(0);
                            // ??????????????????
                            rocketMQTemplate.syncSend(RocketMQConstant.Topic.topic, MessageBuilder.withPayload(order).build(), 10000, 2);
                            // ?????????????????????20??????????????????????????????
                            // ???????????????1???18
                            // 1s 5s 10s 30s 1m 2m 3m 4m 5m 6m 7m 8m 9m 10m 20m 30m 1h 2h
                            // ????????????10s, ????????????20min
                            rocketMQTemplate.syncSend(RocketMQConstant.Topic.delayTopic, MessageBuilder.withPayload(order).build(), 10000, 15);
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
        //????????????-????????????
        String json = hashOperations.get(RedisKeyUtils.SESSIONS_PREFIX, sessionId);
        if (StringUtils.isEmpty(json)) {
            return null;
        }
        //???????????????
        String products = hashOperations.get(RedisKeyUtils.SKUS_PREFIX, sessionId);
        if (StringUtils.isEmpty(products)) {
            //???????????????sku
            return null;
        }

        String[] times = json.split("-");
        List<Sku> skus = JSON.parseArray(products, Sku.class);
        //?????????????????????
        logger.info("products ---->{}", products);
        logger.info("skus--{}", skus);
        if (time >= Long.parseLong(times[0]) && time < Long.parseLong(times[1])) {
            return skus;
        }
        //???????????????
        else if (time < Long.parseLong(times[0])) {
            logger.info("?????????????????????");
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
            logger.info("????????????");
            passed = userBloomFilter.contains(userId);
            logger.info("????????????????????????userId===={}", userBloomFilter.contains(userId));
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
                    // ???????????????,?????????sessionId,?????????????????????????????????
                    if (finalPassed) {
                        logger.info("user is pass:{}", true);
                        session.setSessionId(sessionId);
                    }
                    //?????? session-name
                    session.setStartTime(Long.parseLong(sToEnd[0]));
                    session.setEndTime(Long.parseLong(sToEnd[1]));
                    session.setSessionName(sToEnd[2]);
                    //?????????????????????
                    if (currentTime >= session.getStartTime() && currentTime < session.getEndTime()) {
                        //????????????????????? ????????????sessionName ?????????,id???????????????
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

        // ?????????,???????????????
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
        log.info("?????????????????????????????????{}", RocketMQConstant.Topic.topic);
        return CommonResult.ok();
    }
}
