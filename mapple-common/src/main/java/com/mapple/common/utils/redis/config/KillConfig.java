package com.mapple.common.utils.redis.config;

import com.mapple.common.utils.redis.cons.RedisConstants;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class KillConfig {
    @Resource
    private RedisConstants redisConstants;

    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();

        log.info(redisConstants.getPassword());
        log.info(redisConstants.getHost());
        log.info(redisConstants.getPassword());
        config.useSingleServer()
                .setDatabase(redisConstants.getDatabase())
                .setPassword(redisConstants.getPassword())
                .setAddress("redis://" + redisConstants.getHost() + ":" + redisConstants.getPort());
        return Redisson.create(config);
    }

}
