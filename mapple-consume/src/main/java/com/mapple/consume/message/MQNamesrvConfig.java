package com.mapple.consume.message;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


@Component
public class MQNamesrvConfig {


    @Value("${rocketmq.nameServer}")
    String nameServer;

    /**
     * 返回nameServer地址
     */
    public String nameSrvAddr() {
        return nameServer;
    }
}
