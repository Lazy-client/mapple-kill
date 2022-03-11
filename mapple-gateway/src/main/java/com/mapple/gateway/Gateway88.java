package com.mapple.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication(scanBasePackages = "com.mapple")
@EnableDiscoveryClient
public class Gateway88 {
    public static void main(String[] args) {
        SpringApplication.run(Gateway88.class, args);
    }

}
