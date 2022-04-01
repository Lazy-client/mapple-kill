package io.renren.modules.app.entity.drools;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

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
    private String ruleId;
}
