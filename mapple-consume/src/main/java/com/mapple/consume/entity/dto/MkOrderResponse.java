package com.mapple.consume.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @desc 下单sdk接口返回参数
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MkOrderResponse implements Serializable {

    private static final long serialVersionUID = -5685058946404699059L;

    /**秒杀订单号*/
    private String userId;
    /**用户下单手机号*/
    private String sessionId;
    /**商品id*/
    private String productId;

}
