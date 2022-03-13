package com.mapple.coupon.service.impl;

import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mapple.common.utils.PageUtils;
import com.mapple.common.utils.Query;

import com.mapple.coupon.dao.OrderformDao;
import com.mapple.coupon.entity.OrderformEntity;
import com.mapple.coupon.service.OrderformService;


@Service("orderformService")
public class OrderformServiceImpl extends ServiceImpl<OrderformDao, OrderformEntity> implements OrderformService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<OrderformEntity> page = this.page(
                new Query<OrderformEntity>().getPage(params),
                new QueryWrapper<OrderformEntity>()
        );

        return new PageUtils(page);
    }

}