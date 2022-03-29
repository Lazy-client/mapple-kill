package com.mapple.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mapple.common.utils.PageUtils;
import com.mapple.coupon.entity.ProductSessionEntity;
import com.mapple.coupon.entity.vo.productSessionVo;
import com.mapple.coupon.entity.vo.productSessionVo_new;

import java.util.List;
import java.util.Map;

/**
 * 产品场次关联表
 *
 * @author sicheng
 * @email sicheng_zhou@qq.com
 * @date 2022-03-13 15:23:08
 */
public interface ProductSessionService extends IService<ProductSessionEntity> {

    PageUtils queryPage(Map<String, Object> params);

    String saveProductSession(productSessionVo productSession);

    List<String> saveProductSession_new(productSessionVo_new productSession);

    int deductStock(String productId, String sessionId, Integer productCount);
}

