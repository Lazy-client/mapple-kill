package com.mapple.seckill;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @author zsc
 * @version 1.0
 * @date 2022/2/17 12:25
 */
@EnableDiscoveryClient
@SpringBootApplication(
        scanBasePackages = "com.mapple",
        exclude = {DataSourceAutoConfiguration.class})
public class SeckillApplication {
    public static void main(String[] args) {
        SpringApplication.run(SeckillApplication.class, args);
    }

}
