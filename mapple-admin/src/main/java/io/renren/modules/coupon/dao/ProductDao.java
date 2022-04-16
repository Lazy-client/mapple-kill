package io.renren.modules.coupon.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.renren.modules.coupon.entity.ProductEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 产品信息表
 *
 * @author sicheng
 * @email sicheng_zhou@qq.com
 * @date 2022-03-13 15:23:08
 */
@Mapper
public interface ProductDao extends BaseMapper<ProductEntity> {

}
