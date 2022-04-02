package io.renren.modules.app.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.renren.modules.app.entity.UserEntity;
import io.renren.modules.app.entity.drools.DroolsRulesConfig;

import java.util.List;

/**
 * @author hxx
 * @date 2022/3/31 10:47
 */
public interface DroolsRulesConfigService extends IService<DroolsRulesConfig> {
    String updateRules(DroolsRulesConfig droolsRulesConfig);

    String filterUsers(List<UserEntity> userEntities);
}
