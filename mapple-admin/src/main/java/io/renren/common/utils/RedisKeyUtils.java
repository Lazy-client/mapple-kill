package io.renren.common.utils;

/**
 * @author zsc
 * @version 1.0
 * @date 2022/2/17 20:42
 */
@SuppressWarnings("unused")
public class RedisKeyUtils {
    public static String PUBLIC_ACCOUNT="seckill:account:";
    //# string  产品量
    public static String STOCK_PREFIX = "seckill:upload:stock:";
    //#hash 单个产品的详情 K:sessionId-skuId
    public static String SKU_PREFIX = "seckill:upload:sku:";
    //#hash 场次关联的所有产品的详情 K:sessionId,V:List(场次关联产品的详情，里面有randomCode)
    public static String SKUS_PREFIX = "seckill:upload:skus:";
    //#hash场次    K:sessionId,V:startTime-endTime
    public static String SESSIONS_PREFIX = "seckill:upload:sessions:";
    //#hash秒杀成功的用户 K:userID-randomCode,V:抢购数量
    //redis_seckill_user_prefix:
    public static String SECKILL_USER_PREFIX = "seckill:user:";
    public static String SECKILL_IP_BLACK_LIST = "seckill:ip:black_list";
    public static String JWT_WHITE_LIST = "seckill:jwt:white_list";
}
