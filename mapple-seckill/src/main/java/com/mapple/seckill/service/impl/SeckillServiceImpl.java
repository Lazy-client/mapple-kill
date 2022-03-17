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
    public String kill(String key, String id, String token) throws InterruptedException {
        //id ====> sessionId-productId
        long currentTime = new Date().getTime();
        if (hashOperations.hasKey(RedisKeyUtils.SESSIONS_PREFIX, id)) {//校验场次的sku是否存在
            String skuJason = hashOperations.get(RedisKeyUtils.SESSIONS_PREFIX, id);
            assert skuJason != null;
            Sku sku = JSON.parseObject(skuJason, Sku.class);
            if (currentTime >= sku.getStartTime().getTime() && currentTime < sku.getEndTime().getTime()) {//校验时间
                //todo 解析token拿到userId
                //校验用户是有参与过秒杀
                if (!hashOperations.hasKey(RedisKeyUtils.SECKILL_USER_PREFIX, "userId")) {
                    //分布式锁减库存
                    RSemaphore semaphore = redissonClient.getSemaphore(RedisKeyUtils.STOCK_PREFIX + key);

                    boolean acquire = semaphore.tryAcquire(1, 200, TimeUnit.MILLISECONDS);
                    if (acquire) {//库存与扣成功
                        //todo 替换成userId
                        hashOperations.put(RedisKeyUtils.SECKILL_USER_PREFIX, IdWorker.getId() + "-" + key, "1");
                        //todo 生成订单发消息

                        return "ok";
                    }
                }


            }

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
        if (time >= Long.parseLong(times[0], 19) && time < Long.parseLong(times[1], 19)) {
            return skus;
        }
        //掩盖随机码
        assert skus != null;
        skus.forEach(sku -> sku.setRandomCode(null));
        return skus;
    }

    @Override
    public Map<String, List<Session>> searchSessions() {
        long currentTime = new Date().getTime();
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
                    session.setStartTime(sToEnd[0]);
                    session.setEndTime(sToEnd[0]);
                    //当前秒杀的场次
                    if (currentTime >= Long.parseLong(sToEnd[0]) && currentTime < Long.parseLong(sToEnd[1])) {
                        String skus = hashOperations.get(RedisKeyUtils.SKUS_PREFIX, sessionId);
                        session.setSkus(skus);
                        sessionList.add(session);
                    } else {
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
