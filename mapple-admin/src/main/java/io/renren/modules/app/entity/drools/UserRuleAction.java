package io.renren.modules.app.entity.drools;

import io.renren.modules.app.dao.DroolsLogDao;
import io.renren.modules.app.entity.UserEntity;
import org.drools.core.definitions.rule.impl.RuleImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * 触发Person相关的规则后的处理类
 *
 * @author hxx
 * @date 2022/3/30 18:21
 */
@Component
public class UserRuleAction {


    Map<String, String> RulesContainer = new HashMap<>();
    @Autowired
    static DroolsLogDao droolsLogDao;
    @Autowired
    public static Logger logger = LoggerFactory.getLogger(UserRuleAction.class);

    // 目前只实现记录日志功能
    public static void doParse(UserEntity userEntity, RuleImpl rule) {
        CompletableFuture<Integer> future1 = CompletableFuture.supplyAsync(() -> {
            try {
                //写入日志表中
                DroolsLog droolsLog = new DroolsLog();
                droolsLog.setLog("姓名："+userEntity.getUsername()+" 用户名："+userEntity.getUsername()+" is matched Rule");
                droolsLog.setRuleId();
                droolsLogDao.insert()
                return 0;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return -1;
        });
        logger.error("{} is matched Rule[{}]!", userEntity, rule.getName());
    }
}
