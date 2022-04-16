package io.renren.modules.coupon.entity.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * @author hxx
 * @date 2022/3/23 20:34
 * 在场次内新增产品信息的封装，
 * 包括当前场次下的产品秒杀价格、库存、id
 */
@ApiModel("产品前端交互类_修改产品上架逻辑")
@Data
@NoArgsConstructor
public class productVo_new {
    @ApiModelProperty("产品Id")
    private String productId;
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
}
