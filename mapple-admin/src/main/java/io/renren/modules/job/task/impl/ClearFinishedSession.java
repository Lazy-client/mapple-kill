package io.renren.modules.job.task.impl;

import com.alibaba.fastjson.JSON;
import io.renren.common.utils.DateUtils;
import io.renren.common.utils.RedisKeyUtils;
import io.renren.common.utils.RedisUtils;
import io.renren.modules.job.task.ITask;
import io.renren.modules.job.task.vo.Sku;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author zsc
 * @version 1.0
 * @date 2022/3/25 13:47
 */
@Component("clearFinishedSession")
public class ClearFinishedSession implements ITask {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private HashOperations<String, String, String> hashOperations;
    @Resource
    private RedisUtils redisUtils;

    @Override
    public void run(String params) {
        logger.info("clearFinishedSession 定时任务正在执行，参数为：{}", params);
        long currentTime = System.currentTimeMillis();
        logger.info("当前时间戳{}----时间{}", currentTime,DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
        Map<String, String> entries = hashOperations.entries(RedisKeyUtils.SESSIONS_PREFIX);
        try {
            entries.forEach(
                    (sessionId, v) -> {
                        String[] times = v.split("-");
                        //过期的场次
                        if (currentTime >= Long.parseLong(times[1])) {
                            //清理缓存
                            List<Sku> list = JSON.parseArray(hashOperations.get(RedisKeyUtils.SKUS_PREFIX, sessionId), Sku.class);
                            assert list != null;
                            list.forEach((sku -> {
                                String randomCode = sku.getRandomCode();
                                redisUtils.delete(RedisKeyUtils.STOCK_PREFIX + randomCode);
                                //删除 sku
                                hashOperations.delete(RedisKeyUtils.SKU_PREFIX, sessionId + "-" + sku.getProductId());
                            }));

                            logger.info("正在清理数据");
                            //删除场次关联的skus
                            hashOperations.delete(RedisKeyUtils.SKUS_PREFIX, sessionId);
                            //删除session
                            hashOperations.delete(RedisKeyUtils.SESSIONS_PREFIX, sessionId);
                        }

                    }
            );
            logger.info("success-{}", params);
        } catch (Exception e) {
            logger.error(e.getMessage());
            logger.error("error-{}", params);
        }
    }
}
