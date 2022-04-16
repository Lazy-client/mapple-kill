package com.mapple.consume.service;

import com.baomidou.mybatisplus.extension.api.R;
import com.mapple.consume.service.fallback.FallbackService;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.math.BigDecimal;

/**
 * @author : Gelcon
 * @date : 2022/3/30 18:29
 */
@FeignClient(value = "renren-fast", fallback = FallbackService.class)
@Service
public interface AdminFeignService {

    @GetMapping("/renren-fast/app/user/deductBalance/{userId}/{payAmount}")
    R deductBalance(@PathVariable String userId,
                    @PathVariable BigDecimal payAmount);

    @PostMapping("/renren-fast/coupon/coupon/productsession/deductStock/{productId}/{sessionId}")
    int deductStock(@PathVariable String productId,
                    @PathVariable String sessionId);

    @PostMapping("/renren-fast/coupon/coupon/productsession/refundStock/{productId}/{sessionId}")
    int refundStock(@PathVariable String productId,
                    @PathVariable String sessionId);
}
