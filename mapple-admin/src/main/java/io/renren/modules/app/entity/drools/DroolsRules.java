package io.renren.modules.app.entity.drools;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

import static com.baomidou.mybatisplus.annotation.IdType.ASSIGN_ID;
import static com.baomidou.mybatisplus.annotation.IdType.AUTO;

/**
 * @author hxx
 * @date 2022/3/31 23:58
 */
@Data
@TableName("drools_rules")
public class DroolsRules {
    @TableId(type = ASSIGN_ID)
    private String id;
    private String rules;
    @TableField(fill = FieldFill.INSERT)
    private Date gmtCreate;
}
