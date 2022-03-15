package com.mapple.seckill.controller;

import com.mapple.common.utils.CommonResult;
import com.mapple.seckill.cons.RedisConstants;
import com.mapple.seckill.service.SecKillService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * @author zsc
 * @version 1.0
 * @date 2022/2/17 17:58
 */

@Api(tags = {"秒杀"})
@RestController
public class SeckillController {
    @Resource
    private SecKillService secKillService;

    @ApiOperation(value = "点击秒杀", notes = "随机码一定要传")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "key", value = "秒杀产品的随机码", required = true, dataTypeClass = String.class),
            //@ApiImplicitParam(name = "lostID", value = "ID", required = true, dataTypeClass = Long.class),
            //@ApiImplicitParam(name = "img", value = "照片", required = true, dataTypeClass = MultipartFile.class),
    })
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

    @ApiOperation(value = "获取redis的key", notes = "这只是个测试接口，不是业务")
    @GetMapping("/redisKey")
    public CommonResult redisKey() {
        return Objects.requireNonNull(CommonResult
                .ok()
                .put("redisKey", redisConstants.getPort()))
                .put("redisHost", redisConstants.getHost());
    }
}
