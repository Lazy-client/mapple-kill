package com.mapple.seckill.controller;

import com.alibaba.fastjson.JSON;
import com.mapple.common.utils.redis.cons.RedisConstants;
import com.mapple.common.utils.redis.cons.RedisKeyUtils;
import com.mapple.common.utils.result.CommonResult;
import com.mapple.common.vo.Session;
import com.mapple.common.vo.Sku;
import com.mapple.seckill.service.SecKillService;
import io.swagger.annotations.*;
import org.redisson.api.RMapCache;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
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
    @Value("${ok.kill}")
    private String ok;

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

    @ApiOperation(value = "搜索某个场次下的某个产品详情")
    @GetMapping("searchById")
    public CommonResult searchById(@ApiParam(value = "场次Id", required = true) @RequestParam String sessionId,
                                   @ApiParam(value = "产品Id", required = true) @RequestParam String productId) {

        JSON search = secKillService.searchById(sessionId, productId);
        return CommonResult.ok().put("data", search);

    }

    @ApiOperation(value = "搜索所有场次信息", notes = "进行中，以及未开始")
    @GetMapping("searchSessions")
    public CommonResult searchSessions(@RequestHeader String token) {
        Map<String, List<Session>> data = secKillService.searchSessions(token);
        return CommonResult.ok().put("data", data);
    }

    @Resource
    private RedisConstants redisConstants;

    @Resource
    private RedissonClient redissonClient;

    @ApiOperation(value = "获取redis的key", notes = "这只是个测试接口，不是业务")
    @GetMapping("/redisKey")
    public CommonResult redisKey() {
        RMapCache<Object, Object> userMap = redissonClient.getMapCache(RedisKeyUtils.SECKILL_USER_PREFIX);
        userMap.put("userId" + "-" + "key", "1", 60, TimeUnit.SECONDS);
        String txt =
                "[{\"defaultImg\":\"/image\",\"description\":\"秒杀产品描述\",\"endTime\":1647587757000,\"id\":\"1504667488302624770\",\"productName\":\"秒杀产品3.18 11:54\",\"randomCode\":\"dd96795e80ac499abf467374ad381888\",\"seckillPrice\":10000,\"sessionId\":\"1504658242097872898\",\"sessionName\":\"3.18 11:00场次\",\"startTime\":1647584157000,\"title\":\"标题\",\"totalCount\":50000},{\"defaultImg\":\"/image\",\"description\":\"秒杀产品描述\",\"endTime\":1647587757000,\"id\":\"1504671308004900865\",\"productName\":\"秒杀产品3.18 12:09\",\"randomCode\":\"7c45adef3845440b8b98b57bf24464f9\",\"seckillPrice\":10000,\"sessionId\":\"1504658242097872898\",\"sessionName\":\"3.18 11:00场次\",\"startTime\":1647584157000,\"title\":\"标题\",\"totalCount\":50000},{\"defaultImg\":\"/image\",\"description\":\"这是一款银行存款产品\",\"endTime\":1647587757000,\"id\":\"1504658242097872898\",\"productId\":\"1503967060392857601\",\"productName\":\"银行存款产品\",\"randomCode\":\"3761cb514377431a8c67be78b42a1723\",\"seckillPrice\":100000,\"sessionName\":\"3.18 11:00场次\",\"startTime\":1647584157000,\"title\":\"string\",\"totalCount\":100000}]";
        JSON value = JSON.parseArray(txt);
        return CommonResult.ok()
                .put("data", value)
                .put("common", ok);
    }

    @ApiOperation(value = "测试发送消息")
    @GetMapping("sendOrder")
    public CommonResult sendOrder() {
        return secKillService.sendOrder();
    }

}
