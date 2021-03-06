package com.mapple.coupon.dao;

import com.mapple.coupon.entity.OrderformEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单信息表
 * 
 * @author sicheng
 * @email sicheng_zhou@qq.com
 * @date 2022-03-13 15:23:08
 */
@Mapper
public interface OrderformDao extends BaseMapper<OrderformEntity> {
	
}
