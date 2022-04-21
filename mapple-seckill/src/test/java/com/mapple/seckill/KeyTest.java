package com.mapple.seckill;

import com.alibaba.fastjson.JSON;
import com.mapple.common.utils.LoggerUtil;
import com.mapple.common.utils.Lua;
import com.mapple.common.utils.redis.cons.Key;
import com.mapple.common.utils.redis.cons.RedisKeyUtils;
import com.mapple.common.vo.MkOrder;
import org.junit.jupiter.api.Test;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RMapCache;
import org.redisson.api.RScript;
import org.redisson.api.RedissonClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.HashOperations;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
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
        RBloomFilter<String> user = redissonClient.getBloomFilter("user1");
        user.tryInit(100000000L, 0.01);
        MkOrder mkOrder = new MkOrder();
        mkOrder.setId("1");

        String s = JSON.toJSONString(mkOrder);
        user.add(s);
        LoggerUtil.getLogger().info("是否包含1-----{}", user.contains(s));


        mkOrder.setId("1010");
        String s1 = JSON.toJSONString(mkOrder);
        LoggerUtil.getLogger().info("是否包含XXX-----{}", user.contains(s1));

        user.delete();
        System.out.println(user.isExists());
    }

    @Resource
    RBloomFilter<String> userBloomFilter;

    @Test
    public void test1() {

        userBloomFilter.delete();
        System.out.println(userBloomFilter.isExists());
        userBloomFilter.tryInit(100000000L, 0.01);

        MkOrder mkOrder = new MkOrder();
        mkOrder.setId("1");

        String s = JSON.toJSONString(mkOrder);
        userBloomFilter.add(s);
        LoggerUtil.getLogger().info("是否包含1-----{}", userBloomFilter.contains(s));


        mkOrder.setId("1010");
        String s1 = JSON.toJSONString(mkOrder);
        LoggerUtil.getLogger().info("是否包含XXX-----{}", userBloomFilter.contains(s1));
    }

    @Test
    public void testLua() {
        String banlance = Lua.banlance.getLua();
        RScript script = redissonClient.getScript();
        List<Object> keys = new ArrayList<>();
        keys.add("seckill:banlance:");
        keys.add("userId");
        boolean re = script.eval(
                RScript.Mode.READ_WRITE,
                banlance,
                RScript.ReturnType.BOOLEAN,
                keys,
                "10000");

        System.out.println();
    }
}
