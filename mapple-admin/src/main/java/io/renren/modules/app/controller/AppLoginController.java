/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 * <p>
 * https://www.renren.io
 * <p>
 * 版权所有，侵权必究！
 */

package io.renren.modules.app.controller;


import io.renren.common.utils.LoggerUtil;
import io.renren.common.utils.R;
import io.renren.modules.app.entity.UserEntity;
import io.renren.modules.app.form.LoginForm;
import io.renren.modules.app.service.UserService;
import io.renren.modules.app.service.impl.DroolsRulesConfigServiceImpl;
import io.renren.modules.app.utils.JwtUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.redisson.api.RBloomFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

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

    /**
     * 登录
     */
    @PostMapping("login")
    @ApiOperation("登录(已经加入初筛）")
    public R login(@RequestBody LoginForm form, HttpServletRequest request) {
        //表单校验
//        ValidatorUtils.validateEntity(form);

        //获取request中的ip
        String ipAddress = request.getHeader("x-forwarded-for");
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
            if (ipAddress.equals("127.0.0.1") || ipAddress.equals("0:0:0:0:0:0:0:1")) {
                //根据网卡取本机配置的IP
                InetAddress inet = null;
                try {
                    inet = InetAddress.getLocalHost();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
                ipAddress = inet.getHostAddress();
            }
        }
        //对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
        if (ipAddress != null && ipAddress.length() > 15) { //"***.***.***.***".length() = 15
            if (ipAddress.indexOf(",") > 0) {
                ipAddress = ipAddress.substring(0, ipAddress.indexOf(","));
            }
        }
        LoggerUtil.getLogger().info("user IP {}", ipAddress);
        stringRedisTemplate.opsForValue().set("用户登录ip", ipAddress);

        //用户登录
        UserEntity userEntity = userService.login(form);
        //生成token
        String token = jwtUtils.generateToken(userEntity.getUserId());
        //初筛流程
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


        Map<String, Object> map = new HashMap<>();
        map.put("token", token);
        map.put("expire", jwtUtils.getExpire());
        map.put("user", userEntity);

        return R.ok(map);
    }

}
