package com.mapple.coupon.entity.vo;

import com.mapple.coupon.entity.ProductEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;

/**
 * @author hxx
 * @date 2022/3/23 20:31
 */
@ApiModel("场次，产品关联前端交互类_修改产品上架逻辑 ")
@Data
@NoArgsConstructor
public class productSessionVo_new implements Serializable {
    /**
     * 场次id
     */
    @NotNull
    @ApiModelProperty("场次id")
    private String sessionId;

    /**
     * 场次名称
     */
    @ApiModelProperty("场次名称")
    private String sessionName;

    /**
     * 产品对象
     */
    @ApiModelProperty("产品对象")
    private ArrayList<productVo_new> productList;

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
