package com.mapple.coupon.entity.vo;

import com.baomidou.mybatisplus.annotation.TableId;
import com.mapple.coupon.entity.ProductEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author hxx
 * @date 2022/3/14 14:45
 */
@ApiModel("场次，产品关联前端交互类")
@Data
@NoArgsConstructor
public class productSessionVo implements Serializable {
    /**
     * 场次id
     */
    @NotNull
    @ApiModelProperty("场次id")
    private String sessionId;
    /**
     * 场次id
     */
    @ApiModelProperty("产品id")
    private String productId;

    /**
     * 场次名称
     */
    @ApiModelProperty("场次名称")
    private String sessionName;

    /**
     * 产品对象
     */
    @ApiModelProperty("产品对象")
    private ProductEntity productEntity;
    /**
     * 秒杀产品的总库存量,限制需要>=1
     */
    @Min(value = 1)
    @ApiModelProperty("秒杀产品的总库存量,限制需要>=1")
    private Integer totalCount;

    @Min(value = 0)
    @NotNull
    @ApiModelProperty("秒杀产品的价格")
    private BigDecimal seckillPrice;
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
