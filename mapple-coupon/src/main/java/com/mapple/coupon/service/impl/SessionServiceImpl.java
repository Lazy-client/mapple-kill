package com.mapple.coupon.service.impl;

import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mapple.common.utils.PageUtils;
import com.mapple.common.utils.Query;

import com.mapple.coupon.dao.SessionDao;
import com.mapple.coupon.entity.SessionEntity;
import com.mapple.coupon.service.SessionService;


@Service("sessionService")
public class SessionServiceImpl extends ServiceImpl<SessionDao, SessionEntity> implements SessionService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SessionEntity> page = this.page(
                new Query<SessionEntity>().getPage(params),
                new QueryWrapper<SessionEntity>()
        );

        return new PageUtils(page);
    }

}