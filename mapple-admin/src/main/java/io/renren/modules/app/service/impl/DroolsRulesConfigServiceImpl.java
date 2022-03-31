package io.renren.modules.app.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.renren.modules.app.dao.DroolsRulesConfigDao;
import io.renren.modules.app.dao.UserDao;
import io.renren.modules.app.entity.DroolsRulesConfig;
import io.renren.modules.app.entity.UserEntity;
import io.renren.modules.app.service.DroolsRulesConfigService;
import org.springframework.stereotype.Service;

/**
 * @author hxx
 * @date 2022/3/31 10:47
 */
@Service("DroolsRulesConfigService")
public class DroolsRulesConfigServiceImpl extends ServiceImpl<DroolsRulesConfigDao, DroolsRulesConfig> implements DroolsRulesConfigService {
}
