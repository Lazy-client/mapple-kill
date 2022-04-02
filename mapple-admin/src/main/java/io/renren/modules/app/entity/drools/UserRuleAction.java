package io.renren.modules.app.entity.drools;

import io.renren.common.utils.SpringContextUtils;
import io.renren.modules.app.dao.DroolsLogDao;
import io.renren.modules.app.entity.UserEntity;
import io.renren.modules.app.service.impl.DroolsRulesConfigServiceImpl;
import org.drools.core.definitions.rule.impl.RuleImpl;
import org.redisson.api.RMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * 触发Person相关的规则后的处理类
 *
 * @author hxx
 * @date 2022/3/30 18:21
 */
//@Component
public class UserRuleAction {
//    @Autowired
//    static RMap<String,String> RulesContainer;
//    @Autowired
//    static DroolsLogDao droolsLogDao;
//    @Autowired
//    public static Logger logger = LoggerFactory.getLogger(UserRuleAction.class);

    // 目前只实现记录日志功能
    public static void doParse(UserEntity userEntity,RuleImpl rule) {
        DroolsRulesConfigServiceImpl droolsRulesConfigService = (DroolsRulesConfigServiceImpl) SpringContextUtils.getBean("DroolsRulesConfigService");
        droolsRulesConfigService.asyncExecuteLog(userEntity,1);
    }

}
