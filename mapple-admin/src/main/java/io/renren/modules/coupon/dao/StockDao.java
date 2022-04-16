package io.renren.modules.coupon.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.renren.modules.coupon.entity.StockEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 库存信息表
 *
 * @author sicheng
 * @email sicheng_zhou@qq.com
 * @date 2022-03-13 15:23:08
 */
@Mapper
public interface StockDao extends BaseMapper<StockEntity> {

}
