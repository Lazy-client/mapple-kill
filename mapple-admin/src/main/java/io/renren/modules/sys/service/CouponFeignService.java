package io.renren.modules.sys.service;

import io.renren.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

/**
 * @author zsc
 * @version 1.0
 * @date 2022/3/24 23:43
 */
@FeignClient(value = "mapple-coupon")
@Service
public interface CouponFeignService {
    @GetMapping("/coupon/coupon/product/list")
    public R list(@RequestParam Map<String, Object> params);
}
