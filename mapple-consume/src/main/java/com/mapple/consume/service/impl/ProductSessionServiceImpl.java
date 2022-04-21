package com.mapple.consume.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mapple.consume.entity.ProductSessionEntity;
import com.mapple.consume.mapper.ProductSessionDao;
import com.mapple.consume.service.ProductSessionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Service("productSessionService")
@Slf4j
public class ProductSessionServiceImpl extends ServiceImpl<ProductSessionDao, ProductSessionEntity> implements ProductSessionService {


    @Override
    public int deductStock(String productId, String sessionId, long version) {
        return baseMapper.deductStock(productId, sessionId, version);
    }
}
