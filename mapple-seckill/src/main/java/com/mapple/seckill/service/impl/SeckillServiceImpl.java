package com.mapple.seckill.service.impl;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.mapple.seckill.service.SecKillService;
import org.redisson.api.RSemaphore;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * @author zsc
 * @version 1.0
 * @date 2022/2/17 18:00
 */
@Service
public class SeckillServiceImpl implements SecKillService {
    @Resource
    RedissonClient redissonClient;

    @Resource
    private HashOperations<String, String, Object> hashOperations;
    String STOCK_PREFIX = "seckill:upload:stock:";

    @Override
    public String kill(String key) throws InterruptedException {

        RSemaphore semaphore = redissonClient.getSemaphore(STOCK_PREFIX + key);
        boolean acquire = semaphore.tryAcquire(1, 50, TimeUnit.MILLISECONDS);
        if (acquire) {
            hashOperations.put("seckill:user:", IdWorker.getId() + "-" + key, "1");
            return "ok";
        }


        return null;
    }
}
