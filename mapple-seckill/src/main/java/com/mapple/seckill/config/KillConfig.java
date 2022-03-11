package com.mapple.seckill.config;

import com.mapple.seckill.cons.RedisConstants;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * @author zsc
 * @version 1.0
 * @date 2022/2/17 12:35
 */

@Configuration
public class KillConfig {
    @Resource
    private RedisConstants redisConstants;
    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        config.useSingleServer()
                .setDatabase(redisConstants.getDatabase())
                .setPassword(redisConstants.getPassword())
                .setAddress("redis://" + redisConstants.getHost() + ":" + redisConstants.getPort());
        return Redisson.create(config);
    }

}
