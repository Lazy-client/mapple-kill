package io.renren.common.redisson;

import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author zsc
 * @version 1.0
 * @date 2022/4/1 14:24
 */
@Configuration
public class UserBloomFilter {
    @Bean
    public RBloomFilter<String> userBloomFilter(RedissonClient redissonClient){
        RBloomFilter<String> user = redissonClient.getBloomFilter("user");
        user.tryInit(1000000L,0.01);
        return user;
    }
}
