package com.mapple.common.utils.redis.config;

import org.redisson.api.RBloomFilter;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author zsc
 * @version 1.0
 * @date 2022/4/1 14:24
 */
@Configuration
public class UserBloomFilterConfig {
    @Bean
    public RBloomFilter<String> userBloomFilter(RedissonClient redissonClient){
        RBloomFilter<String> userBloomFilter = redissonClient.getBloomFilter("user");
        userBloomFilter.tryInit(10000000L,0.01);
        return userBloomFilter;
    }

    @Bean
    public RMap<String,String> ruleMap(RedissonClient redissonClient){
        return redissonClient.getMap("rule");
    }
}
