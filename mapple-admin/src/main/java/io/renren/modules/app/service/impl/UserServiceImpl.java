/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 * <p>
 * https://www.renren.io
 * <p>
 * 版权所有，侵权必究！
 */

package io.renren.modules.app.service.impl;

import cn.hutool.core.util.IdcardUtil;
import cn.hutool.crypto.SmUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.RemovalListener;
import io.renren.common.exception.RRException;
import io.renren.common.utils.PageUtils;
import io.renren.common.utils.Query;
import io.renren.common.validator.Assert;
import io.renren.common.validator.ValidatorUtils;
import io.renren.modules.app.dao.UserDao;
import io.renren.modules.app.entity.UserEntity;
import io.renren.modules.app.form.LoginForm;
import io.renren.modules.app.form.RegisterForm;
import io.renren.modules.app.service.UserService;
import io.renren.modules.app.utils.CacheConstants;
import io.renren.modules.app.utils.CacheUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;


@Service("userService")
public class UserServiceImpl extends ServiceImpl<UserDao, UserEntity> implements UserService {

    @Autowired
    public RedisTemplate<String, String> stringRedisTemplate;

    //绑定userbalance的hash
    BoundHashOperations<String, String, String> operationsForBalance;

    @Autowired
    public UserServiceImpl(RedisTemplate<String, String> stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
        operationsForBalance = stringRedisTemplate.boundHashOps("USER_BALANCE");
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        String username = (String) params.get("username");
        IPage<UserEntity> page = this.page(
                new Query<UserEntity>().getPage(params),
                new QueryWrapper<UserEntity>()
                        .like(StringUtils.isNotBlank(username), "username", username)
        );

        return new PageUtils(page);
    }

    @Override
    public UserEntity queryByUsername(String username) {
        return baseMapper.selectOne(new QueryWrapper<UserEntity>().eq("username", username));
    }

    @Override
    public UserEntity login(LoginForm form) {
        UserEntity user = queryByUsername(form.getUsername());
        Assert.isNull(user, "用户名错误");

        //密码错误
        if (!user.getPassword().equals(SmUtil.sm3(form.getPassword()))) {
            throw new RRException("密码错误");
        }

        //如果没错，则返回用户唯一id
        return user;
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
//        //redis lua脚本减库存，绑定userbalance的hash，写个script
//        //redission判断hash为USER_BALANCE的key的value是否大于payAmount
//
//        //如果更新成功，则返回true，否则返回false
//        //如果大于，则执行脚本，并返回true，否则返回false
//        DefaultRedisScript<Boolean> script = new DefaultRedisScript<>();
//        script.setScriptText("if redis.call('get', KEYS[1]) > ARGV[1] then return redis.call('decrby', KEYS[1], ARGV[1]) else return false end");
//        script.setResultType(Boolean.class);
//        Boolean result = stringRedisTemplate.execute(script, operationsForBalance.getKey(), payAmount.toString());
//        if (result) {
//            //扣钱成功
//            return R.ok();
//        }
//        "if redis"
//        String script = "if redis.call('HGET', KEYS[1]) == ARGV[1] then return redis.call('HINCRBY', KEYS[1], -ARGV[2]) else return -1 end";
//        //设置脚本缓存时间
//        stringRedisTemplate.setDefaultScriptTimeout(5000);
//        //获取hash中的余额
//        String balance = operationsForBalance.get(userId);
//        //设置hash中的余额
//        operationsForBalance.put(userId, balance);
//        //执行脚本
//        Long result = stringRedisTemplate.execute(
//                DefaultRedisScript.of(script, Long.class),
//
//                        operationsForBalance.getKey(),
//                                balance,
//                                                payAmount.toString());
//        //如果执行失败，则返回失败
//        if (result == -1) {
//            return R.error("扣款失败");
//        }
//        //如果执行成功，则返回成功
//        return R.ok();
//
//        BigDecimal balance = new BigDecimal(operationsForBalance.get(userId));
//        if (balance.compareTo(payAmount) < 0) {
//            throw new RRException("余额不足");
//        }
//        operationsForBalance.put(userId, balance.subtract(payAmount).toString());
//        return R.ok();

//        BigDecimal balance = (BigDecimal) CacheUtil.get(CacheConstants.GET_MENU, userId);

        UserEntity user = this.getById(userId);
        // 钱不够
        if (user.getBalance().compareTo(payAmount) < 0)
            return R.failed("当前账户余额不足，请充值");
        int result = baseMapper.deductBalance(userId, payAmount);
        if (result > 0)
            return R.ok("支付成功");
        return R.failed("支付失败");
    }

    /**
     * 放余额到caffeine cache
     *
     * @param userId
     * @param balance
     */
    @Override
    public void UploadBalanceToCaffeine(String userId, BigDecimal balance) {
////        System.out.println(cache.get(2, new Function<Integer, Integer>() {
////            @Override
////            public Integer apply(Integer key) {
////                return 888;
////            }
////        }));
//        CacheUtil.put(CacheConstants.GET_MENU,userId,balance);
        if (!Boolean.TRUE.equals(operationsForBalance.hasKey(userId))) {
            operationsForBalance.put(userId, balance.toString());
        }

//    @Override
//    public void UploadBalanceToRedis(String userId, BigDecimal balance){
//        if (!Boolean.TRUE.equals(operationsForBalance.hasKey(userId))){
//            operationsForBalance.put(userId,balance.toString());
//        }
//    }
    }
}
