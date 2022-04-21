package io.renren.modules.app.controller;

import io.renren.common.utils.JwtUtilsInCommon;
import io.renren.common.utils.R;

import io.renren.modules.app.entity.UserEntity;
import io.renren.modules.app.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * @author hxx
 * @date 2022/3/23 23:19
 */
@RestController
@RequestMapping("/app/user")
@Api("Mk用户信息查询接口")
public class UserController {
    @Autowired
    public RedisTemplate<String, String> stringRedisTemplate;
    //绑定userBalance的hash
    BoundHashOperations<String, String, String> operationsForBalance;

    @Autowired
    public UserController(RedisTemplate<String, String> stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
        operationsForBalance = stringRedisTemplate.boundHashOps("seckill:balance:");
    }
    @Resource
    private UserService userService;

    @ApiOperation(value = "扣减账户余额")
    @Deprecated
    @GetMapping("deductBalance/{userId}/{payAmount}")
    public void deductBalance(@PathVariable String userId,
                           @PathVariable BigDecimal payAmount) {
//        return userService.deductBalance(userId, payAmount);
    }

    @ApiOperation(value = "用户刷新接口")
    @GetMapping("getUserByToken")
    public R getUserByToken(@RequestHeader String token) {
        String userId = JwtUtilsInCommon.getUserIdByToken(token);
        UserEntity user = userService.getById(userId);
        if (Boolean.TRUE.equals(operationsForBalance.hasKey(userId))) {
            user.setBalance(new BigDecimal(Objects.requireNonNull(operationsForBalance.get(userId))));
        }
        return R.ok().put("user",user);
    }
}
