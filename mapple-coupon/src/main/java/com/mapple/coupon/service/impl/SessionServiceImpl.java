package com.mapple.coupon.service.impl;

import cn.hutool.core.date.DateUtil;
import com.mapple.common.exception.RRException;
import com.mapple.common.utils.redis.cons.RedisKeyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mapple.common.utils.PageUtils;
import com.mapple.common.utils.Query;

import com.mapple.coupon.dao.SessionDao;
import com.mapple.coupon.entity.SessionEntity;
import com.mapple.coupon.service.SessionService;
import org.springframework.transaction.annotation.Transactional;


@Service("sessionService")
public class SessionServiceImpl extends ServiceImpl<SessionDao, SessionEntity> implements SessionService {

    @Autowired
    public SessionDao sessionDao;

    @Autowired
    public RedisTemplate<String, Object> redisTemplate;

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
    @Transactional
    public String saveSession(SessionEntity session) {
        Date startTime = session.getStartTime();
        long startTime_long = startTime.getTime();
        String startTime_str = DateUtil.format(startTime, "yyyy-MM-dd HH:mm:ss");
        Date endTime = session.getEndTime();
        long endTime_long = endTime.getTime();
        String endTime_str = DateUtil.format(endTime, "yyyy-MM-dd HH:mm:ss");
        Date nowTime = new Date();

        //当前时间在开始时间之前，且开始时间在结束时间之前，时间设置没问题
        if (nowTime.compareTo(startTime)<0&&startTime.compareTo(endTime)<0){
            //插入mysql成功
            Integer count = sessionDao.selectCount(new QueryWrapper<SessionEntity>().eq("start_time", startTime_str).eq("end_time", endTime_str));
            if (count==0){
                int insertSession = sessionDao.insert(session);
                if (insertSession==1){
                    //获取sessionId
                    String sessionId = session.getId();
                    //不存在该session时
                    BoundHashOperations<String, Object, Object> operations = redisTemplate.boundHashOps(RedisKeyUtils.SESSIONS_PREFIX);
                    if (!operations.hasKey(sessionId)){
                        //把场次放入redis中
                        try {
                            operations.put(sessionId,startTime_long+"-"+endTime_long+"-"+session.getSessionName());
                            return sessionId;
                        }catch (Exception e){
                            throw new RRException("数据插入错误");
                        }
                    }
                }
            }
            else {
                throw new RRException("场次重复");
            }
        }
        throw new RRException("时间设置有误");
    }

}
