package com.mapple.consume.service;

import com.mapple.common.utils.result.CommonResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * @author : Gelcon
 * @date : 2022/3/29 20:43
 */
@FeignClient(value = "mapple-coupon")
@Service
public interface CouponFeignService {
    /**
     * 全路径：下面三个部分组成
     * 项目根路径 /coupon
     * Controller路径 /coupon/productsession
     * 具体接口路径 /deductStock/{productId}/{sessionId}/{productCount}
     */
    @PostMapping("/coupon/coupon/productsession/deductStock/{productId}/{sessionId}/{productCount}")
    public int deductStock(@PathVariable String productId,
                                    @PathVariable String sessionId,
                                    @PathVariable Integer productCount);
}
