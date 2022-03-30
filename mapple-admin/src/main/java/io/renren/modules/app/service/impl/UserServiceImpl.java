/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 * <p>
 * https://www.renren.io
 * <p>
 * 版权所有，侵权必究！
 */

package io.renren.modules.app.service.impl;

import com.baomidou.mybatisplus.extension.api.R;
import cn.hutool.core.util.IdcardUtil;
import cn.hutool.crypto.SmUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.renren.common.exception.RRException;
import io.renren.common.validator.Assert;
import io.renren.common.validator.ValidatorUtils;
import io.renren.modules.app.dao.UserDao;
import io.renren.modules.app.entity.UserEntity;
import io.renren.modules.app.form.LoginForm;
import io.renren.modules.app.form.RegisterForm;
import io.renren.modules.app.service.UserService;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Random;


@Service("userService")
public class UserServiceImpl extends ServiceImpl<UserDao, UserEntity> implements UserService {

    @Override
    public UserEntity queryByUsername(String username) {
        return baseMapper.selectOne(new QueryWrapper<UserEntity>().eq("username", username));
    }

    @Override
    public String login(LoginForm form) {
        UserEntity user = queryByUsername(form.getUsername());
        Assert.isNull(user, "用户名错误");

        //密码错误
        if (!user.getPassword().equals(SmUtil.sm3(form.getPassword()))) {
            throw new RRException("密码错误");
        }

        //如果没错，则返回用户唯一id
        return user.getUserId();
    }

    @Override
    public String register(RegisterForm form) {
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
        UserEntity userEntity = queryByUsername(form.getUsername());
        if (null == userEntity) {
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
        user.setAge(new Random().nextInt(101) + 15);
        //密码加密
        user.setPassword(SmUtil.sm3(form.getPassword()));
        if (save(user)) {
            return user.getUserId();
        } else {
            throw new RRException("插入错误");
        }
    }

    @Override
    public R deductBalance(String userId, BigDecimal payAmount) {
        UserEntity user = this.getById(userId);
        // 钱不够
        if (user.getBalance().compareTo(payAmount) < 0)
            return R.failed("当前账户余额不足，请充值");
        int result = baseMapper.deductBalance(userId, payAmount);
        if (result > 0)
            return R.ok("支付成功");
        return R.failed("支付失败");
    }
}
