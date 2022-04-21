package com.mapple.consume.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;

@Configuration
public class BeanConfig {
    @Bean(name = "deleteHashAndZSetLua")
    public DefaultRedisScript deleteHashAndZSetLua(){
        DefaultRedisScript<Long> longDefaultRedisScript = new DefaultRedisScript<>();
        longDefaultRedisScript.setResultType(Long.class);
        longDefaultRedisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("luascript/delete-hashAndZset-redis.lua")));
        return longDefaultRedisScript;
    }
}
