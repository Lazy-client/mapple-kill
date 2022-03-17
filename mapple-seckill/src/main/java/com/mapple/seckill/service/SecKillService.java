package com.mapple.seckill.service;

import com.mapple.common.vo.Session;
import com.mapple.common.vo.Sku;

import java.util.List;

/**
 * @author zsc
 * @version 1.0
 * @date 2022/2/17 17:59
 */
public interface SecKillService {
    String kill(String key) throws InterruptedException;

    List<Sku> search(String sessionId);

    List<Session> searchSessions();
}
