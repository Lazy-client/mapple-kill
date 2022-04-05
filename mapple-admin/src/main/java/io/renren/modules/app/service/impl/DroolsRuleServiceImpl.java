package io.renren.modules.app.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.renren.modules.app.dao.DroolsRulesDao;
import io.renren.modules.app.entity.drools.DroolsRules;
import io.renren.modules.app.service.DroolsRulesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author hxx
 * @date 2022/4/5 21:07
 */
@Service
public class DroolsRuleServiceImpl extends ServiceImpl<DroolsRulesDao, DroolsRules> implements DroolsRulesService {
    @Autowired
    private DroolsRulesDao droolsRulesDao;
    @Override
    public List<String> selectNames() {
        //查询10条规则名称，按时间先后排序，返回结果
        List<String> list = droolsRulesDao.selectNames();
        return list;
    }
}
