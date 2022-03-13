package com.mapple.coupon.service.impl;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mapple.common.utils.PageUtils;
import com.mapple.common.utils.Query;

import com.mapple.coupon.dao.ProductSessionDao;
import com.mapple.coupon.entity.ProductSessionEntity;
import com.mapple.coupon.service.ProductSessionService;


@Service("productSessionService")
public class ProductSessionServiceImpl extends ServiceImpl<ProductSessionDao, ProductSessionEntity> implements ProductSessionService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        QueryWrapper<ProductSessionEntity> productSessionEntityQueryWrapper = new QueryWrapper<>();
        //获取场地id
        String sessionId = (String) params.get("sessionId");
        //判空
        if (!StringUtils.isBlank(sessionId)){

        }
        IPage<ProductSessionEntity> page = this.page(
                new Query<ProductSessionEntity>().getPage(params),
                new QueryWrapper<ProductSessionEntity>()
        );

        return new PageUtils(page);
    }

}