package com.mapple.seckill.service;

import com.alibaba.fastjson.JSON;
import com.mapple.common.vo.Session;
import com.mapple.common.vo.Sku;

import java.util.List;
import java.util.Map;

/**
 * @author zsc
 * @version 1.0
 * @date 2022/2/17 17:59
 */
public interface SecKillService {
    String kill(String key,String id,String token) throws InterruptedException;

    List<Sku> search(String sessionId);

    Map<String, List<Session>> searchSessions(String token);

    JSON searchById(String sessionId, String productId);
}
