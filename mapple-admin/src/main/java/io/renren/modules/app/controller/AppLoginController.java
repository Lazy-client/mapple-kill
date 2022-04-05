/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 * <p>
 * https://www.renren.io
 * <p>
 * 版权所有，侵权必究！
 */

package io.renren.modules.app.controller;


import io.renren.common.exception.RRException;
import io.renren.common.utils.LoggerUtil;
import io.renren.common.utils.R;
import io.renren.modules.app.entity.UserEntity;
import io.renren.modules.app.form.LoginForm;
import io.renren.modules.app.service.UserService;
import io.renren.modules.app.service.impl.DroolsRulesConfigServiceImpl;
import io.renren.modules.app.utils.HttpUtils;
import io.renren.modules.app.utils.JwtUtils;
import io.renren.modules.sys.entity.SysConfigEntity;
import io.renren.modules.sys.service.SysConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.redisson.api.RBloomFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * APP登录授权
 *
 * @author Mark sunlightcs@gmail.com
 */
@RestController
@RequestMapping("/app")
@Api("APP登录接口")
public class AppLoginController {
    @Autowired
    private UserService userService;
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private PersonRuleController personRuleController;

    @Resource
    private RBloomFilter<String> userBloomFilter;

    @Autowired
    DroolsRulesConfigServiceImpl droolsRulesConfigService;

    @Autowired
    public RedisTemplate<String, String> stringRedisTemplate;

    //    //redis hash操作绑定ip
//    BoundHashOperations<String, Object, Object> operationsForIp;
//
//    @Autowired
//    public AppLoginController(RedisTemplate<String, String> stringRedisTemplate) {
//        this.stringRedisTemplate = stringRedisTemplate;
//        operationsForIp = stringRedisTemplate.boundHashOps("IP_PREFIX")
//    }
    //redis hash操作绑定ip
    BoundHashOperations<String, Object, Object> operationsForIp;

    @Autowired
    public AppLoginController(RedisTemplate<String, String> stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
        operationsForIp = stringRedisTemplate.boundHashOps("IP_PREFIX");
    }

    @Resource
    private SysConfigService sysConfigService;

    /**
     * 登录
     */
    @PostMapping("login")
    @ApiOperation("登录(已经加入初筛）")
    public R login(@RequestBody LoginForm form, HttpServletRequest request) throws ExecutionException, InterruptedException {
        //表单校验

        //获取request中的ip
        CompletableFuture<String> future = getConfigById("2");
        CompletableFuture<String> ipConfig = getConfigById("3");
        //
        String clientIP = HttpUtils.getClientIP(request);
        stringRedisTemplate.opsForValue().set("clientIP", clientIP);

        //用户登录
        UserEntity userEntity = userService.login(form);
        //生成token
        String token = jwtUtils.generateToken(userEntity.getUserId());

        String ipIsOpen = ipConfig.get();
        if (Objects.equals(ipIsOpen, "true")) {//开启ip登录限制
            if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(clientIP))) {
                if (!Objects.equals(stringRedisTemplate.opsForValue().get(clientIP), userEntity.getUserId())) {
                    throw new RRException("请勿多账号频繁登录，1分钟后重试");
                }
            } else {
                //放入redis，设置过期时间1分钟
                stringRedisTemplate.opsForValue().set(clientIP, userEntity.getUserId(), 60, java.util.concurrent.TimeUnit.SECONDS);
            }
        }
        String ruleIsOpen = future.get();
        if ("true".equals(ruleIsOpen)) {
            //初筛流程
            LoggerUtil.getLogger().info("初筛流程开始");
            ArrayList<UserEntity> userEntities = new ArrayList<>();
            userEntities.add(userEntity);
            R r = personRuleController.filterManyUserByRules(userEntities);
            if (Objects.equals(r.get("result"), "pass")) {
                String userIdPass = userEntity.getUserId();
                //布隆过滤器将通过初筛的人加入到白名单快速过滤
                userBloomFilter.add(userIdPass);
                LoggerUtil.getLogger().info("userId===={} pass", userIdPass);
            } else {
                //记录未通过的用户log
                droolsRulesConfigService.asyncExecuteLog(userEntity, 0);
            }
        }


        Map<String, Object> map = new HashMap<>();
        map.put("token", token);
        map.put("expire", jwtUtils.getExpire());
        map.put("user", userEntity);

        return R.ok(map);
    }

    private CompletableFuture<String> getConfigById(String id) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                SysConfigEntity sysConfigEntity = sysConfigService.getById(id);
                return sysConfigEntity.getParamValue();
            } catch (Exception ignored) {
            }
            return null;
        });
    }

    /**
     * 退出
     */
    @PostMapping("logout")
    @ApiOperation("退出")
    public R logout() {
        //todo redis中删除ip
        return R.ok();
    }

}
