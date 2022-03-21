/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package io.renren.modules.app.controller;


import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.IdcardUtil;
import cn.hutool.crypto.SmUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.renren.common.exception.RRException;
import io.renren.common.utils.R;
import io.renren.common.validator.ValidatorUtils;
import io.renren.modules.app.dao.UserDao;
import io.renren.modules.app.entity.UserEntity;
import io.renren.modules.app.form.RegisterForm;
import io.renren.modules.app.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Random;

/**
 * 注册
 *
 * @author Mark sunlightcs@gmail.com
 */
@RestController
@RequestMapping("/app")
@Api("APP注册接口")
public class AppRegisterController {
    @Autowired
    private UserService userService;

    @PostMapping("register")
    @ApiOperation("注册")
    public R register(@RequestBody RegisterForm form){
        //表单校验
        ValidatorUtils.validateEntity(form);

        //校验身份证号
        if (!IdcardUtil.isValidCard(form.getIdCard())) {
            throw new RRException("身份证号格式错误");
        }
        UserEntity user = new UserEntity();
//        //生成16位用户名随机码
//        String simpleUUID = IdUtil.simpleUUID();
        //设置用户名
        //username用户名查重
        UserEntity userEntity = userService.queryByUsername(form.getUsername());
        if (null==userEntity){
            user.setUsername(form.getUsername());
        }
        //设置余额
        user.setBalance(new BigDecimal(10000));
        user.setRealName(form.getRealName());
        //有工作
        user.setNotHasJob(false);
        //未逾期
        user.setIsOverdue(false);
        //没有失信
        user.setIsDishonest(false);
        //身份证号
        user.setIdCard(form.getIdCard());
        //电话号码
        user.setTelephoneNum(form.getTelephoneNum());
        //随机生成年龄：[15,115]岁
        user.setAge(new Random().nextInt(101)+ 15);
        //密码加密
        user.setPassword(SmUtil.sm3(form.getPassword()));
        userService.save(user);

        return R.ok();
    }
}
