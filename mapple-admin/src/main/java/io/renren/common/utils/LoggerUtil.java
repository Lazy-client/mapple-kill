package io.renren.common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author zsc
 * @version 1.0
 * @date 2022/3/30 20:50
 */
public class LoggerUtil {
    protected static Logger logger = LoggerFactory.getLogger(LoggerUtil.class);
    public static Logger getLogger(){
        return logger;
    }

}
