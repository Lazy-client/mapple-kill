package com.mapple.common.vo;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author hxx
 * @date 2022/3/14 14:45
 */
@ApiModel("场次，产品关联前端交互类")
@Data
@NoArgsConstructor
public class Session implements Serializable {

    @NotNull
    @ApiModelProperty("场次id")
    private String sessionId;
    @ApiModelProperty("产品名称")
    private String productName;
    @ApiModelProperty("当前商品秒杀的开始时间")
    private String startTime;
    @ApiModelProperty("当前商品秒杀的结束时间")
    private String endTime;
    @ApiModelProperty("场次关联产品的json串")
    private String skus;

}
