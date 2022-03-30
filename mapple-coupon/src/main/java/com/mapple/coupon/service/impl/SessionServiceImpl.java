package com.mapple.coupon.service.impl;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mapple.common.exception.RRException;
import com.mapple.common.utils.PageUtils;
import com.mapple.common.utils.Query;
import com.mapple.common.utils.redis.RedisUtils;
import com.mapple.common.utils.redis.cons.RedisKeyUtils;
import com.mapple.common.vo.Sku;
import com.mapple.coupon.dao.SessionDao;
import com.mapple.coupon.entity.SessionEntity;
import com.mapple.coupon.service.SessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("sessionService")
public class SessionServiceImpl extends ServiceImpl<SessionDao, SessionEntity> implements SessionService {

    @Autowired
    public SessionDao sessionDao;

    @Autowired
    public RedisTemplate<String, Object> redisTemplate;

    @Resource
    public HashOperations<String, String, String> hashOperations;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        long time = System.currentTimeMillis();
        IPage<SessionEntity> page = this.page(
                new Query<SessionEntity>().getPage(params),
                new QueryWrapper<>()
        );


        page.setRecords(page
                .getRecords()
                .stream()
                .peek(e -> {
                    if (time < e.getStartTime().getTime())
                        e.setSessionStatus("未开始");
                    else if (time >= e.getStartTime().getTime() && time < e.getEndTime().getTime())
                        e.setSessionStatus("正在进行中");
                    else if (time > e.getEndTime().getTime())
                        e.setSessionStatus("已结束");
                })
                .sorted((e1, e2) -> e2.getGmtCreate().compareTo(e1.getGmtCreate()))
                .collect(Collectors.toList()));
        return new PageUtils(page);
    }

    /**
     * 存储场次信息
     *
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
        if (nowTime.compareTo(startTime) < 0 && startTime.compareTo(endTime) < 0) {
            //插入mysql成功
            Integer count = sessionDao.selectCount(new QueryWrapper<SessionEntity>().eq("start_time", startTime_str).eq("end_time", endTime_str));
            if (count == 0) {
                int insertSession = sessionDao.insert(session);
                if (insertSession == 1) {
                    //获取sessionId
                    String sessionId = session.getId();
                    //不存在该session时
                    BoundHashOperations<String, Object, Object> operations = redisTemplate.boundHashOps(RedisKeyUtils.SESSIONS_PREFIX);
                    if (!operations.hasKey(sessionId)) {
                        //把场次放入redis中
                        try {
                            operations.put(sessionId, startTime_long + "-" + endTime_long + "-" + session.getSessionName());
                            return sessionId;
                        } catch (Exception e) {
                            throw new RRException("数据插入错误");
                        }
                    }
                }
            } else {
                throw new RRException("场次重复");
            }
        }
        throw new RRException("时间设置有误");
    }

    @Resource
    RedisUtils redisUtils;

    @Override
    @Transactional
    public void delete(List<String> ids) {
        long currentTime = System.currentTimeMillis();
        List<SessionEntity> sessionEntities = listByIds(ids);
        sessionEntities.forEach(sessionEntity -> {
            if (currentTime >=
                    sessionEntity.getStartTime().getTime()) {
                throw new RRException("只允许删除未开始的场次");
            }
        });
        boolean suc = removeByIds(ids);
        if (!suc)
            throw new RRException("删除失败,无法删除不存在的");
        Map<String, String> entries = hashOperations.entries(RedisKeyUtils.SESSIONS_PREFIX);
        entries.keySet()
                .stream()
                .filter(ids::contains)
                .forEach(
                        (sessionId) -> {
                            String[] times = entries.get(sessionId).split("-");
                            //未开始的场次
                            if (currentTime < Long.parseLong(times[0])) {
                                //清理缓存
                                List<Sku> list = JSON.parseArray(hashOperations.get(RedisKeyUtils.SKUS_PREFIX, sessionId), Sku.class);
                                assert list != null;
                                list.forEach((sku -> {
                                    String randomCode = sku.getRandomCode();
                                    redisUtils.delete(RedisKeyUtils.STOCK_PREFIX + randomCode);
                                    //删除 sku
                                    hashOperations.delete(RedisKeyUtils.SKU_PREFIX, sessionId + "-" + sku.getProductId());
                                }));
                                //删除场次关联的skus
                                hashOperations.delete(RedisKeyUtils.SKUS_PREFIX, sessionId);
                                //删除session
                                hashOperations.delete(RedisKeyUtils.SESSIONS_PREFIX, sessionId);
                            }
                        }
                );
    }
}
