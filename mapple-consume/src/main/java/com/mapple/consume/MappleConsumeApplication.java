package com.mapple.consume;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.mapple"})
@EnableDiscoveryClient  // Nacos
@EnableFeignClients //OpenFeign
public class MappleConsumeApplication {

    public static void main(String[] args) {
        SpringApplication.run(MappleConsumeApplication.class, args);
    }

}
