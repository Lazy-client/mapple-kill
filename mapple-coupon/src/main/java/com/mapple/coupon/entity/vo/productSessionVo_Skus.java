package com.mapple.coupon.entity.vo;

import com.mapple.coupon.entity.ProductEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author hxx
 * @date 2022/3/14 14:45
 */
@ApiModel("场次，产品关联前端交互类(skus使用)")
@Data
@NoArgsConstructor
public class productSessionVo_Skus implements Serializable {
    /**
     * 场次名称
     */
    @ApiModelProperty("场次名称")
    private String sessionName;

    /**
     * 产品对象
     */
    @ApiModelProperty("产品id")
    private String productId;

    /**
     * 产品对象
     */
    @ApiModelProperty("产品名称")
    private String productName;

    /**
     * 秒杀产品的价格
     */
    @Min(value = 0)
    @ApiModelProperty("秒杀产品的价格")
    private BigDecimal seckillPrice;

    /**
     * 产品介绍描述
     */
    @ApiModelProperty("产品介绍描述")
    private String description;

    /**
     * 产品默认图片地址
     */
    @ApiModelProperty("产品默认图片地址")
    private String defaultImg;

    /**
     * 标题
     */
    @ApiModelProperty("标题")
    private String title;

    /**
     * 秒杀产品的总库存量,限制需要>=1
     */
    @Min(value = 1)
    @ApiModelProperty("秒杀产品的总库存量,限制需要>=1")
    private Integer totalCount;


    /**
     * 当前商品秒杀的开始时间
     */
    @ApiModelProperty("当前商品秒杀的开始时间")
    private Date startTime;

    /**
     * 当前商品秒杀的结束时间
     */
    @ApiModelProperty("当前商品秒杀的结束时间")
    private Date endTime;

    /**
     * 当前商品秒杀的随机码
     */
    @ApiModelProperty("当前商品秒杀的随机码")
    private String randomCode;
}
