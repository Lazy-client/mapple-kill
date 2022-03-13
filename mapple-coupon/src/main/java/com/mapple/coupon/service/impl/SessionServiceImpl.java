package com.mapple.coupon.service.impl;

import com.mapple.common.exception.RRException;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    public SessionDao sessionDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SessionEntity> page = this.page(
                new Query<SessionEntity>().getPage(params),
                new QueryWrapper<SessionEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 存储场次信息
     * @param session
     * @return
     */
    @Override
    public String saveSession(SessionEntity session) {
        long startTime = session.getStartTime().getTime();
        long endTime = session.getEndTime().getTime();
        long now = System.currentTimeMillis();
        //当前时间在开始时间之前，且开始时间在结束时间之前，时间设置没问题
        if (now<startTime&&startTime<endTime){
            //插入mysql成功
            if (sessionDao.insert(session)==1){
                return session.getId();
            }
        }else {
            return null;
        }
        return null;
    }

}