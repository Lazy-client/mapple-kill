package com.mapple.consume.message;

import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author snowalker
 * @date 2018/9/16
 * @desc 基础协议类
 */
@Data
public abstract class BaseMsg {

    public Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    /**版本号，默认1.0*/
    private String version = "1.0";
    /**主题名*/
    private String topicName;

    public abstract String encode();

    public abstract void decode(String msg);
}


