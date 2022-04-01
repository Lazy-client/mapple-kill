package io.renren.modules.app.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.renren.modules.app.entity.drools.DroolsLog;
import io.renren.modules.app.entity.drools.DroolsRulesConfig;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author hxx
 * @date 2022/4/2 1:35
 */
@Mapper
public interface DroolsLogDao extends BaseMapper<DroolsLog> {
}
