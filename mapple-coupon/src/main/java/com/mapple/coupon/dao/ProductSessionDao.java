package com.mapple.coupon.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mapple.coupon.entity.ProductSessionEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * 产品场次关联表
 *
 * @author sicheng
 * @email sicheng_zhou@qq.com
 * @date 2022-03-13 15:23:08
 */
@Mapper
public interface ProductSessionDao extends BaseMapper<ProductSessionEntity> {
    @Update("update mk_product_session " +
            "set total_count = total_count - 1 " +
            "where product_id = #{productId} and session_id = #{sessionId}")
    int deductStock(@Param("productId") String productId, @Param("sessionId") String sessionId);
}
