package io.renren.modules.app.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.renren.modules.app.entity.drools.DroolsRules;
import io.renren.modules.app.entity.drools.DroolsRulesConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author hxx
 * @date 2022/4/1 0:02
 */
@Mapper
public interface DroolsRulesDao extends BaseMapper<DroolsRules> {
    @Select("select rule_name from drools_rules order by gmt_create desc limit 10")
    List<String> selectNames();
}
