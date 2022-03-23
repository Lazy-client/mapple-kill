package com.mapple.common.utils.redis.cons;

/**
 * @author zsc
 * @version 1.0
 * @date 2022/3/24 0:42
 */
public enum Key {
    //# string  产品量
    STOCK_PREFIX,
    //#hash 单个产品的详情 K:sessionId-skuId
    SKU_PREFIX,
    //#hash 场次关联的所有产品的详情 K:sessionId,V:List(场次关联产品的详情，里面有randomCode)
    SKUS_PREFIX,
    //#hash场次    K:sessionId,V:startTime-endTime
    SESSIONS_PREFIX,
    //#hash秒杀成功的用户 K:userID-randomCode,V:抢购数量
    //redis_seckill_user_prefix:
    SECKILL_USER_PREFIX,
    SECKILL_IP_BLACK_LIST,
    JWT_WHITE_LIST;
}
