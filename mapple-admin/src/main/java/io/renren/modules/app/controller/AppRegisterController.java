/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package io.renren.modules.app.controller;


import cn.binarywang.tools.generator.ChineseIDCardNumberGenerator;
import cn.binarywang.tools.generator.ChineseMobileNumberGenerator;
import cn.binarywang.tools.generator.ChineseNameGenerator;
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
import java.util.concurrent.TimeUnit;

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
        String userId=userService.register(form);
        return R.ok().put("userId",userId);
    }

//    @PostMapping("/user/list")

    @PostMapping("test")
    public void test(){
        for (int i = 0; i <= 2000; i++) {
            String idCard = ChineseIDCardNumberGenerator.getInstance().generate();
            String generatedMobileNum = ChineseMobileNumberGenerator.getInstance()
                    .generate();
            String generatedName = ChineseNameGenerator.getInstance().generate();
            RegisterForm registerForm = new RegisterForm();
            registerForm.setRealName(generatedName);
            registerForm.setTelephoneNum(generatedMobileNum);
            registerForm.setIdCard(idCard);
            registerForm.setPassword("123456");
            registerForm.setUsername("user"+i);
            userService.register(registerForm);
            if (i%100==0){
                try {
                    TimeUnit.MILLISECONDS.sleep(1000);}catch (InterruptedException e){ e.printStackTrace();}
            }
        }
    }
}
