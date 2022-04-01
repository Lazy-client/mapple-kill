package io.renren.common.redisson;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @author zsc
 * @version 1.0
 * @date 2022/3/10 22:06
 */
@Component
@RefreshScope
@Data
@ConfigurationProperties(prefix = "spring.redis")
public class RedisConstants {
    private String host;
    private String port;
    private String password;
    private Integer database;

    public static String HOST;
    public static String PORT;
    public static String ENV;
    public static int DATABASE;
    public static String PASSWORD;

    @PostConstruct
    public void init() {
        HOST = host;
        PORT = port;
        DATABASE = database;
        PASSWORD = password;
    }
}
