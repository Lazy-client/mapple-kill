package com.mapple.seckill.controller;

import com.mapple.common.utils.CommonResult;
import com.mapple.common.vo.Session;
import com.mapple.common.vo.Sku;
import com.mapple.common.utils.redis.cons.RedisConstants;
import com.mapple.seckill.service.SecKillService;
import io.swagger.annotations.*;
import org.redisson.api.RMapCache;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author zsc
 * @version 1.0
 * @date 2022/2/17 17:58
 */

@Api(tags = {"秒杀"})
@RestController
public class SeckillController {
    protected Logger logger = LoggerFactory.getLogger(getClass());
    @Resource
    private SecKillService secKillService;

    @Resource
    private HashOperations<String, String, Object> hashOperations;

    @ApiOperation(value = "点击秒杀", notes = "随机码一定要传")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "key", value = "秒杀产品的随机码", required = true, dataTypeClass = String.class),
            @ApiImplicitParam(name = "token", value = "用户唯一标识", required = true, dataTypeClass = String.class),
            @ApiImplicitParam(name = "sessionId", value = "场次Id", required = true, dataTypeClass = String.class),
            @ApiImplicitParam(name = "productId", value = "产品Id", required = true, dataTypeClass = String.class),
    })
    @GetMapping("kill/{token}/{key}")
    public CommonResult kill(@PathVariable String key,
                             String sessionId,
                             String productId,
                             @PathVariable String token) {
        String s = null;
        try {
            logger.info(token);
            s = secKillService.kill(key, sessionId.concat("-").concat(productId), token);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (s == null) {
            return CommonResult.error("秒杀失败");
        }
        return CommonResult.ok().put("ok", s);
    }

    @ApiOperation(value = "搜索某个场次下的产品详情")
    @GetMapping("search")
    public CommonResult search(@ApiParam(value = "场次Id", required = true) @RequestParam String sessionId) {

        List<Sku> search = secKillService.search(sessionId);
        return CommonResult.ok().put("data", search);

    }

    @ApiOperation(value = "搜索所有场次信息", notes = "进行中，以及未开始")
    @GetMapping("searchSessions")
    public CommonResult searchSessions() {
        Map<String, List<Session>> data = secKillService.searchSessions();
        return CommonResult.ok().put("data", data);
    }

    @Resource
    private RedisConstants redisConstants;

    @Resource
    private RedissonClient redissonClient;

    @ApiOperation(value = "获取redis的key", notes = "这只是个测试接口，不是业务")
    @GetMapping("/redisKey")
    public CommonResult redisKey() {
        RMapCache<String, String> map = redissonClient.getMapCache("JWT_WHITE_LIST");
        map.put("hello","hello",30, TimeUnit.SECONDS);
        map.put("ok","你好");
        String hello = map.get("hello");
        String ok = map.get("ok");
        logger.info(hello);
        logger.info(ok);

        return Objects.requireNonNull(CommonResult
                .ok()
                .put("redisKey", redisConstants.getPort()))
                .put("redisHost", hashOperations.hasKey("seckill:upload:skus:", "场次Id_c"));
    }
}
