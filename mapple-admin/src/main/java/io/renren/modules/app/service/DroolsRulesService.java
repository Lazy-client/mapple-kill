package io.renren.modules.app.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.renren.modules.app.entity.drools.DroolsRules;

import java.util.List;

/**
 * @author hxx
 * @date 2022/4/5 21:07
 */
public interface DroolsRulesService extends IService<DroolsRules> {
    List<String> selectNames();
}
