package com.mapple.seckill.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.mapple.common.utils.RedisKeyUtils;
import com.mapple.common.vo.Session;
import com.mapple.common.vo.Sku;
import com.mapple.seckill.service.SecKillService;
import org.redisson.api.RSemaphore;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.HashOperations;
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
public class SeckillServiceImpl implements SecKillService {


    protected Logger logger = LoggerFactory.getLogger(getClass());
    @Resource
    RedissonClient redissonClient;

    @Resource
    private HashOperations<String, String, String> hashOperations;

    @Override
    public String kill(String key) throws InterruptedException {

        RSemaphore semaphore = redissonClient.getSemaphore(RedisKeyUtils.STOCK_PREFIX + key);
        boolean acquire = semaphore.tryAcquire(1, 200, TimeUnit.MILLISECONDS);
        if (acquire) {
            hashOperations.put("seckill:user:", IdWorker.getId() + "-" + key, "1");
            return "ok";
        }


        return null;
    }

    @Override
    public List<Sku> search(String sessionId) {
        long time = new Date().getTime();
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
        if (time > Long.parseLong(times[0], 19) && time < Long.parseLong(times[1], 19)) {
            return skus;
        }
        //掩盖随机码
        assert skus != null;
        skus.forEach(sku -> sku.setRandomCode(null));
        return skus;
    }

    @Override
    public List<Session> searchSessions() {
        long currentTime = new Date().getTime();
        Map<String, String> sessions = hashOperations.entries(RedisKeyUtils.SESSIONS_PREFIX);
        Set<String> sessionIds = sessions.keySet();
        List<Session> sessionList = new ArrayList<>();
        sessionIds.forEach(
                sessionId -> {
                    String times = sessions.get(sessionId);
                    String[] sToEnd = times.split("-");
                    Session session = new Session();
                    //当前秒杀的场次
                    if (currentTime >= Long.parseLong(sToEnd[0]) && currentTime < Long.parseLong(sToEnd[1])) {
                        String skus = hashOperations.get(RedisKeyUtils.SKUS_PREFIX, sessionId);
                        session.setSkus(skus);
                    }
                    session.setSessionId(sessionId);
                    //设置 session-name
                    session.setStartTime(sToEnd[0]);
                    session.setEndTime(sToEnd[0]);
                    sessionList.add(session);
                }
        );
        return sessionList;
    }
}
