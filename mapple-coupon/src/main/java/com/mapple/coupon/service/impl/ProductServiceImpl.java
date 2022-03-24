package com.mapple.coupon.service.impl;

import com.mapple.common.exception.RRException;
import org.springframework.stereotype.Service;

import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mapple.common.utils.PageUtils;
import com.mapple.common.utils.Query;

import com.mapple.coupon.dao.ProductDao;
import com.mapple.coupon.entity.ProductEntity;
import com.mapple.coupon.service.ProductService;


@Service("productService")
public class ProductServiceImpl extends ServiceImpl<ProductDao, ProductEntity> implements ProductService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<ProductEntity> page = this.page(
                new Query<ProductEntity>().getPage(params),
                new QueryWrapper<ProductEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveProduct(ProductEntity product) {
        String depositTime = product.getDepositTime();
        String pattern = "\\d{1,2}-\\d{1,2}";
        if (!depositTime.matches(pattern)) {
            throw new RRException("存期格式错误");
        }
        String[] strs = depositTime.split("-");
        int year = Integer.parseInt(strs[0]);
        int month = Integer.parseInt(strs[1]);
        if (year <= 0 || year >= 100 || month < 0 || month >= 12) {
            throw new RRException("存期格式错误");
        }
        save(product);
    }

}