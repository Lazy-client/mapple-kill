package com.mapple.seckill;

import com.alibaba.fastjson.JSON;
import com.mapple.common.utils.LoggerUtil;
import com.mapple.common.utils.redis.cons.Key;
import com.mapple.common.utils.redis.cons.RedisKeyUtils;
import com.mapple.common.vo.MkOrder;
import org.junit.jupiter.api.Test;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RMapCache;
import org.redisson.api.RedissonClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.HashOperations;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * @author zsc
 * @version 1.0
 * @date 2022/3/24 0:53
 */
@SpringBootTest
public class KeyTest {
    @Resource
    RedissonClient redissonClient;
    @Resource
    private HashOperations<String, String, String> hashOperations;

    @Test
    public void test() {
        System.out.println(Key.SKU_PREFIX.name());
        RMapCache<Object, Object> userMap = redissonClient.getMapCache(RedisKeyUtils.SECKILL_USER_PREFIX);
        userMap.put("userId" + "-" + "key", "1", 60, TimeUnit.SECONDS);
//        System.out.println(hashOperations.hasKey(RedisKeyUtils.SECKILL_USER_PREFIX, "userId" + "-" + "key"));
    }

    @Test
    public void filterTest() {
        RBloomFilter<String> user = redissonClient.getBloomFilter("user");
//        user.tryInit(100000000L, 0.01);
        MkOrder mkOrder = new MkOrder();
        mkOrder.setId("1");

        String s = JSON.toJSONString(mkOrder);
        user.add(s);
        LoggerUtil.getLogger().info("是否包含1-----{}", user.contains(s));



        mkOrder.setId("1010");
        String s1 = JSON.toJSONString(mkOrder);
//        user.add(s1);
        LoggerUtil.getLogger().info("是否包含XXX-----{}", user.contains(s1));


    }
}
