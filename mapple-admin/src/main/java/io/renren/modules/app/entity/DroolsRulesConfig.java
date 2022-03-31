package io.renren.modules.app.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.io.Serializable;

import static com.baomidou.mybatisplus.annotation.IdType.ASSIGN_ID;

/**
 * @author hxx
 * @date 2022/3/31 9:34
 */
@TableName("drools_config")
@Data
public class DroolsRulesConfig implements Serializable {
    @TableId(type = ASSIGN_ID)
    @ApiModelProperty(value = "规则id",required = false)
    private String id;

    @ApiModelProperty(value = "规则名称",required = false)
    private String ruleName;

    @Max(104)
    @ApiModelProperty(value = "年龄上界",required = true)
    private Integer ageMax;

    @Min(15)
    @ApiModelProperty(value = "年龄下界",required = true)
    private Integer ageMin;

    @ApiModelProperty(value = "是否有工作，false表示有",required = true)
    private boolean jobValue;

    @ApiModelProperty(value = "是否欠款逾期，false表示没逾期",required = true)
    private boolean overdueValue;

    @Min(0)
    @ApiModelProperty(value = "存款下界",required = true)
    private Integer balanceMin;

    @ApiModelProperty(value = "是否失信，false表示没有失信",required = true)
    private boolean dishonestValue;
}
