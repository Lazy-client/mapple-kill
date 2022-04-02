package com.mapple.consume.service;

import com.baomidou.mybatisplus.extension.api.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.math.BigDecimal;

/**
 * @author : Gelcon
 * @date : 2022/3/30 18:29
 */
@FeignClient(value = "renren-fast")
@Service
public interface AdminFeignService {

    @GetMapping("/renren-fast/app/user/deductBalance/{userId}/{payAmount}")
    R deductBalance(@PathVariable String userId,
                    @PathVariable BigDecimal payAmount);


}
