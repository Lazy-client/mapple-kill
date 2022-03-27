package com.mapple.consume.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author : Gelcon
 * @date : 2022/3/20 10:08
 * 配置类
 */
@Configuration
@MapperScan("com.mapple.consume.mapper")
public class ConsumeConfig {

}
