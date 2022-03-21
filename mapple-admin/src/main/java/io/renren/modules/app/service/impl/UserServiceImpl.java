/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package io.renren.modules.app.service.impl;


import cn.hutool.crypto.SmUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.renren.common.exception.RRException;
import io.renren.common.validator.Assert;
import io.renren.modules.app.dao.UserDao;
import io.renren.modules.app.entity.UserEntity;
import io.renren.modules.app.form.LoginForm;
import io.renren.modules.app.service.UserService;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;


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
		if(!user.getPassword().equals(SmUtil.sm3(form.getPassword()))){
			throw new RRException("密码错误");
		}

		//如果没错，则返回用户唯一id
		return user.getUserId();
	}
}
