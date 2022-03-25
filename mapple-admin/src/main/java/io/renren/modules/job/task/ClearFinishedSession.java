package io.renren.modules.job.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @author zsc
 * @version 1.0
 * @date 2022/3/25 13:47
 */
@Component("clearFinishedSession")
public class ClearFinishedSession implements ITask {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void run(String params) {
        logger.info("clearFinishedSession 定时任务正在执行，参数为：{}", params);



    }
}
