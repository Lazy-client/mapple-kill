package io.renren.modules.job.task.impl;

import com.alibaba.fastjson.JSON;
import io.renren.common.utils.DateUtils;
import io.renren.common.utils.RedisKeyUtils;
import io.renren.modules.job.task.ITask;
import io.renren.modules.job.task.vo.Sku;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
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
    private ValueOperations<String, String> valueOperations;

    @Override
    public void run(String params) {
        logger.info("clearFinishedSession 定时任务正在执行，参数为：{}", params);
        long currentTime = System.currentTimeMillis();
        logger.info("当前时间戳{}----时间{}", currentTime, DateUtils.format(new Date(), DateUtils.DATE_TIME_PATTERN));
        Map<String, String> entries = hashOperations.entries(RedisKeyUtils.SESSIONS_PREFIX);
        try {
            entries.forEach(
                    (sessionId, v) -> {
                        String[] times = v.split("-");
                        //过期的场次
                        if (currentTime >= Long.parseLong(times[1])) {
                            //清理缓存
                            List<Sku> list = JSON.parseArray(hashOperations.get(RedisKeyUtils.SKUS_PREFIX, sessionId), Sku.class);
                            if (list != null && !list.isEmpty()) {
                                ArrayList<String> stockKeys = new ArrayList<>();
                                Object[] skuKeys = new String[list.size()];
                                for (int i = 0; i < list.size(); i++) {
                                    String randomCode = list.get(i).getRandomCode();
                                    skuKeys[i] = sessionId + "-" + list.get(i).getProductId();
                                    stockKeys.add(RedisKeyUtils.STOCK_PREFIX + randomCode);
                                }
                                //删除 sku
                                logger.info("正在{}清理数据", times[2]);
                                hashOperations.delete(RedisKeyUtils.SKU_PREFIX, skuKeys);
                                valueOperations.getOperations().delete(stockKeys);
                                //删除场次关联的skus
                                hashOperations.delete(RedisKeyUtils.SKUS_PREFIX, sessionId);
                                //删除session
                                hashOperations.delete(RedisKeyUtils.SESSIONS_PREFIX, sessionId);
                                logger.info("{}清理数据结束", times[2]);
                            }
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
