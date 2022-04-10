package com.mapple.consume.service.fallback;

import com.mapple.common.utils.result.CommonResult;
import com.mapple.consume.service.CouponFeignService;
import org.springframework.stereotype.Component;

/**
 * @author hxx
 * @date 2022/3/30 16:08
 */
@Component
public class FallbackService implements CouponFeignService {
    @Override
    public int deductStock(String productId, String sessionId) {
        return -1;
    }

    @Override
    public int refundStock(String productId, String sessionId) {
        return -1;
    }
}
