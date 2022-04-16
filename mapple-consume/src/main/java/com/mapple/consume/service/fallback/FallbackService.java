package com.mapple.consume.service.fallback;

import com.baomidou.mybatisplus.extension.api.R;
import com.mapple.consume.service.AdminFeignService;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * @author hxx
 * @date 2022/3/30 16:08
 */
@Component
public class FallbackService implements AdminFeignService {

    @Override
    public R deductBalance(String userId, BigDecimal payAmount) {
        return null;
    }

    @Override
    public int deductStock(String productId, String sessionId) {
        return -1;
    }

    @Override
    public int refundStock(String productId, String sessionId) {
        return -1;
    }
}
