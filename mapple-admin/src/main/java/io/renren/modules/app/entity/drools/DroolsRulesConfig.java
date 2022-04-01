package io.renren.modules.app.entity.drools;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.io.Serializable;

import static com.baomidou.mybatisplus.annotation.IdType.ASSIGN_ID;
import static com.baomidou.mybatisplus.annotation.IdType.AUTO;

/**
 * @author hxx
 * @date 2022/3/31 9:34
 */
@TableName("drools_config")
@Data
@ApiModel("初筛规则配置类")
public class DroolsRulesConfig implements Serializable {
    @TableId(type = AUTO)
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

    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "是否需要有工作，false表示需要，true表示无所谓",required = true)
    private String jobValue;

    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "是否欠款逾期，false表示需要用户不逾期，true表示无所谓",required = true)
    private String overdueValue;

    @Min(0)
    @ApiModelProperty(value = "存款下界",required = true)
    private Integer balanceMin;

    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "是否失信，false表示需要非失信人，true表示无所谓",required = true)
    private String dishonestValue;
}
