package io.renren.modules.app.entity.drools;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

import static com.baomidou.mybatisplus.annotation.IdType.ASSIGN_ID;

/**
 * @author hxx
 * @date 2022/4/2 1:31
 */
@Data
@TableName("drools_log")
public class DroolsLog {
    @TableId(type = ASSIGN_ID)
    private String id;
    private String log;
    private String username;
    private String ruleId;
    @ApiModelProperty(value = "通过状态，true通过，false不通过")
    private Boolean passStatus;
    private String ruleName;
    @TableField(fill = FieldFill.INSERT)
    private Date gmtCreate;

}
