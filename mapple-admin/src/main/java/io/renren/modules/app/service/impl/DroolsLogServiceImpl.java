package io.renren.modules.app.service.impl;

import io.renren.modules.app.dao.DroolsLogDao;
import io.renren.modules.app.entity.drools.DroolsLog;
import io.renren.modules.app.service.DroolsLogService;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * @author hxx
 * @date 2022/4/2 16:14
 */
@Service
public class DroolsLogServiceImpl extends ServiceImpl<DroolsLogDao, DroolsLog>  implements DroolsLogService {
}
