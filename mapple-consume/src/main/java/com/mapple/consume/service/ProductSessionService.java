package com.mapple.consume.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mapple.consume.entity.ProductSessionEntity;

/**
 * 产品场次关联表
 *
 * @author sicheng
 * @email sicheng_zhou@qq.com
 * @date 2022-03-13 15:23:08
 */
public interface ProductSessionService extends IService<ProductSessionEntity> {

    int deductStock(String productId, String sessionId, long version);

}

