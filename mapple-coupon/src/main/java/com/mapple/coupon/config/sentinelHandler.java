package com.mapple.coupon.config;

import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.mapple.common.utils.result.CommonResult;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * @author hxx
 * @date 2022/3/30 10:50
 */
public class sentinelHandler {
    public static CommonResult handlerExceptionSentinel(BlockException exception) {
        return CommonResult.error(4444,"滚开，你被限流了");
    }
}
