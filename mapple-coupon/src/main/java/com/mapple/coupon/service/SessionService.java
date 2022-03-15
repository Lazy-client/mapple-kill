package com.mapple.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mapple.common.utils.PageUtils;
import com.mapple.coupon.entity.SessionEntity;

import java.util.Map;

/**
 * 场次表
 *
 * @author sicheng
 * @email sicheng_zhou@qq.com
 * @date 2022-03-13 15:23:08
 */
public interface SessionService extends IService<SessionEntity> {

    PageUtils queryPage(Map<String, Object> params);

    String saveSession(SessionEntity session);
}

