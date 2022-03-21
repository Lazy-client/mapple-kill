package com.mapple.coupon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author zsc
 * @version 1.0
 * @date 2022/2/17 12:25
 */
@EnableDiscoveryClient
@SpringBootApplication(
        scanBasePackages = "com.mapple")
//        exclude = {DataSourceAutoConfiguration.class}
@ComponentScan(basePackages = {"com.mapple.common","com.mapple.coupon"})
public class CouponApplication {
    public static void main(String[] args) {
        SpringApplication.run(CouponApplication.class, args);
    }

}
