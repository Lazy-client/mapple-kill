package com.mapple.consume.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 * 订单信息表
 * </p>
 *
 * @author Gelcon
 * @since 2022-03-19
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="MkOrder对象", description="订单信息表")
public class MkOrder implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键id")
      @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;
    @ApiModelProperty(value = "随机码")
    private String randomCode;

    @ApiModelProperty(value = "场次id")
    private String sessionId;

    @ApiModelProperty(value = "场次id")
    private String sessionName;

    @ApiModelProperty(value = "产品id")
    private String productId;

    @ApiModelProperty(value = "场次id")
    private String productName;

    @ApiModelProperty(value = "用户id")
    private String userId;

    @ApiModelProperty(value = "事务id")
    private String transactionId;

    @ApiModelProperty(value = "订单号，与在数据库中的id区分开来，供用户使用")
    private String orderSn;

    @ApiModelProperty(value = "产品购买数量")
    private Integer productCount;

    @ApiModelProperty(value = "订单总额")
    private BigDecimal totalAmount;

    @ApiModelProperty(value = "应付总额")
    private BigDecimal payAmount;

    @ApiModelProperty(value = "支付方式【1->微信；2->支付宝；3->银联； 4->货到付款；】")
    private Integer payType;

    @ApiModelProperty(value = "订单状态【0->待付款；1->待发货；2->已发货；3->已完成；4->已关闭；5->无效订单】")
    private Integer status;

    @ApiModelProperty(value = "自动确认时间（天）")
    private Integer autoConfirmDay;

    @ApiModelProperty(value = "订单备注")
    private String note;

    @ApiModelProperty(value = "逻辑删除 1（true）已删除， 0（false）未删除")
    @TableLogic
    private Integer isDeleted;

    @ApiModelProperty(value = "支付时间")
      @TableField(fill = FieldFill.INSERT)
    private Date gmtCreate;

    @ApiModelProperty(value = "更新时间")
      @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date gmtModified;


}
