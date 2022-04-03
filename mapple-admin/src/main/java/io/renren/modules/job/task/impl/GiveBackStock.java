package io.renren.modules.job.task.impl;

import io.renren.common.utils.RedisKeyUtils;
import io.renren.modules.job.task.ITask;
import org.redisson.api.RSemaphore;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author zsc
 * @version 1.0
 * @date 2022/4/3 17:16
 */

@Component
public class GiveBackStock implements ITask {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private RedissonClient redissonClient;
    @Override
    public void run(String params) {
        // todo 查询mysql中20min内未支付的订单
        logger.info("定时关闭订单,归还库存 定时任务正在执行，参数为：{}", params);
        // todo 后面就是减库存,将randomCode替换为查出来的randomCode
        RSemaphore stock = redissonClient.getSemaphore(RedisKeyUtils.STOCK_PREFIX + "randomCode");
        stock.release(1);
    }
}
