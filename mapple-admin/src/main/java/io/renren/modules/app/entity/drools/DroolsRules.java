package io.renren.modules.app.entity.drools;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import static com.baomidou.mybatisplus.annotation.IdType.AUTO;

/**
 * @author hxx
 * @date 2022/3/31 23:58
 */
@Data
@TableName("drools_rules")
public class DroolsRules {
    @TableId(type = AUTO)
    private Integer id;
    private String rules;
}
