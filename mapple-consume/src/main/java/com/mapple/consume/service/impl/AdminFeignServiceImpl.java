package com.mapple.consume.service.impl;

import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mapple.consume.entity.UserEntity;
import com.mapple.consume.mapper.UserDao;
import com.mapple.consume.service.AdminFeignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.math.BigDecimal;

/**
 * @author hxx
 * @date 2022/4/21 11:06
 */
public class AdminFeignServiceImpl extends ServiceImpl<UserDao, UserEntity> implements AdminFeignService {

    @Autowired
    public RedisTemplate<String, String> stringRedisTemplate;

    //绑定userbalance的hash
    BoundHashOperations<String, String, String> operationsForBalance;

    @Autowired
    public AdminFeignServiceImpl(RedisTemplate<String, String> stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
        operationsForBalance = stringRedisTemplate.boundHashOps("USER_BALANCE");
    }
    @Override
    public R deductBalance(String userId, BigDecimal payAmount) {
        operationsForBalance

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
