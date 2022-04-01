package io.renren.modules.app.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.renren.modules.app.entity.drools.DroolsRules;
import io.renren.modules.app.entity.drools.DroolsRulesConfig;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author hxx
 * @date 2022/4/1 0:02
 */
@Mapper
public interface DroolsRulesDao extends BaseMapper<DroolsRules> {
}
