package io.renren.modules.job.task;

import com.alibaba.fastjson.JSON;
import io.renren.common.utils.RedisKeyUtils;
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

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private HashOperations<String, String, String> hashOperations;

    @Override
    public void run(String params) {
        long currentTime = new Date().getTime();
        logger.info("clearFinishedSession 定时任务正在执行，参数为：{}", params);
        Map<String, String> entries = hashOperations.entries(RedisKeyUtils.SESSIONS_PREFIX);
        entries.forEach(
                (sessionId, v) -> {
                    String[] times = v.split("-");
                    //过期的场次
                    if (currentTime >= Long.parseLong(times[1])) {
                        //清理缓存
                        List<Sku> list = JSON.parseArray(hashOperations.get(RedisKeyUtils.SKUS_PREFIX, sessionId), Sku.class);
                        assert list != null;
                        list.forEach((sku -> {
                            //删除 sku
                            hashOperations.delete(sessionId + "-" + sku.getProductId());

                        }));
                    }
                    //删除场次关联的skus
                    hashOperations.delete(RedisKeyUtils.SKUS_PREFIX, sessionId);
                    //删除session
                    hashOperations.delete(RedisKeyUtils.SESSIONS_PREFIX, sessionId);
                }
        );

        logger.info("success-{}", params);
    }
}
