package com.mapple.common.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author : Gelcon
 * @date : 2022/4/21 11:11
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "订单支付对象", description = "用于消息队列订单支付")
public class MkOrderPay implements Serializable {

    private static final long serialVersionUID = 1807252068118871358L;

    // 订单Id
    private String id;

    private String sessionId;

    private String productId;

    private String userId;

    private BigDecimal payAmount;

    private String orderSn;
}
