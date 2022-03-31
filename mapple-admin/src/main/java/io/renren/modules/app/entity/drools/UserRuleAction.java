package io.renren.modules.app.entity.drools;

import io.renren.modules.app.entity.Person;
import io.renren.modules.app.entity.UserEntity;
import org.drools.core.definitions.rule.impl.RuleImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 触发Person相关的规则后的处理类
 *
 * @author hxx
 * @date 2022/3/30 18:21
 */
public class UserRuleAction {
    public static Logger logger = LoggerFactory.getLogger(UserRuleAction.class);

    // 目前只实现记录日志功能
    public static void doParse(UserEntity userEntity, RuleImpl rule) {
        logger.debug("{} is matched Rule[{}]!", userEntity, rule.getName());
    }
}
