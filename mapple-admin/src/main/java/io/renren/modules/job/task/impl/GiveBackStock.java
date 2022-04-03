package io.renren.modules.job.task.impl;

import io.renren.common.utils.RedisKeyUtils;
import io.renren.modules.clients.ConsumeFeignService;
import io.renren.modules.job.task.ITask;
import io.seata.spring.annotation.GlobalTransactional;
import org.redisson.api.RSemaphore;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

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

    @Resource
    private ConsumeFeignService consumeFeignService;

    @Override
    @GlobalTransactional
    public void run(String timeout) {
        logger.info("定时关闭订单,归还库存 定时任务正在执行，参数为：{}分钟", timeout);
        long currentTime = System.currentTimeMillis();
        // todo 查询mysql中 timeout 内未支付的订单，并删除这些过期的订单
        // orders,替换成远程调用拿到的,具体就是查出订单 ===== 当前时间- 订单创建时间>timeout && 订单状态是未支付
        //keys 是随机码
        List<String> keys = consumeFeignService.getTimeOrders(Long.getLong(timeout) * 60 * 1000, currentTime);
        //归还库存
        if (keys != null && keys.size() > 0) {
            keys.forEach(key -> {
                RSemaphore stock = redissonClient.getSemaphore(RedisKeyUtils.STOCK_PREFIX + key);
                //归还产品的一个库存
                stock.release(1);
            });
        }
    }
}
