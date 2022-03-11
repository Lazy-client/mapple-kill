package com.mapple.seckill.controller;

import com.mapple.common.utils.CommonResult;
import com.mapple.seckill.cons.RedisConstants;
import com.mapple.seckill.service.SecKillService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author zsc
 * @version 1.0
 * @date 2022/2/17 17:58
 */

@RestController
public class SeckillController {
    @Resource
    private SecKillService secKillService;

    @GetMapping("kill/{key}")
    public CommonResult kill(@PathVariable String key) {
        String s = null;
        try {
            s = secKillService.kill(key);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (s == null) {
            return CommonResult.error("秒杀失败");
        }
        return CommonResult.ok().put("ok", s);
    }
    @Resource
    private RedisConstants redisConstants;

    @GetMapping("/redisKey")
    public CommonResult redisKey() {
        return CommonResult
                .ok()
                .put("redisKey", redisConstants.getPort());
    }
}
