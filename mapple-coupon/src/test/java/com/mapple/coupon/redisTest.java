package com.mapple.coupon;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;

/**
 * @author hxx
 * @date 2022/3/15 11:20
 */
@SpringBootTest
public class redisTest {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Test
    public void method(){
        redisTemplate.opsForValue().set("key","value");
    }
}