package io.renren.modules.clients.cons;

/**
 * @author zsc
 * @version 1.0
 * @date 2022/3/27 14:52
 */
@SuppressWarnings("unused")
public enum ServiceName {
    MAPPLE_SECKILL("mapple-seckill"),
    MAPPLE_ADMIN("renren-fast"),
    MAPPLE_COUPON("mapple-coupon"),
    MAPPLE_CONSUME("mapple-consumer"),
    MAPPLE_GATEWAY("mapple-gateway");


    public final String service;

    ServiceName(String service) {
        this.service = service;
    }

    public String getService() {
        return this.service;
    }
}
