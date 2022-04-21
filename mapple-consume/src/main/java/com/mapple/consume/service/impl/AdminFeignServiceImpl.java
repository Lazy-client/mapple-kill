package com.mapple.consume.service.impl;

import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mapple.common.utils.Lua;
import com.mapple.common.utils.redis.cons.RedisKeyUtils;
import com.mapple.consume.entity.UserEntity;
import com.mapple.consume.mapper.UserDao;
import com.mapple.consume.service.AdminFeignService;
import org.redisson.api.RScript;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @author hxx
 * @date 2022/4/21 11:06
 */
@Service
public class AdminFeignServiceImpl extends ServiceImpl<UserDao, UserEntity> implements AdminFeignService {

    @Resource
    RedissonClient redissonClient;

    @Autowired
    public RedisTemplate<String, String> stringRedisTemplate;

    //绑定userbalance的hash
    BoundHashOperations<String, String, String> operationsForBalance;

    @Autowired
    public AdminFeignServiceImpl(RedisTemplate<String, String> stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
        operationsForBalance = stringRedisTemplate.boundHashOps(RedisKeyUtils.USER_BALANCE);
    }
    @Override
    public R deductBalance(String userId, BigDecimal payAmount) {
        String banlance = Lua.banlance.getLua();
        RScript script = redissonClient.getScript();
        List<Object> keys = new ArrayList<>();
        keys.add(RedisKeyUtils.USER_BALANCE);
        keys.add(userId);
        boolean re = script.eval(
                RScript.Mode.READ_WRITE,
                banlance,
                RScript.ReturnType.BOOLEAN,
                keys,
                payAmount.toString());
        if (re) {
            //发支付消息
            //todo
            return R.ok("支付成功");
        }
        return R.failed("支付失败");
    }
}
