package io.renren.modules.app.utils;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.renren.modules.app.dao.DroolsRulesConfigDao;
import io.renren.modules.app.entity.DroolsRulesConfig;
import io.renren.modules.app.entity.UserEntity;
import io.renren.modules.app.service.DroolsRulesConfigService;
import lombok.extern.slf4j.Slf4j;
import org.drools.template.ObjectDataCompiler;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.internal.io.ResourceFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigDecimal;

import cn.hutool.core.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.drools.template.ObjectDataCompiler;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.utils.KieHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

/**
 * @author hxx
 * @date 2022/3/31 9:15
 * <p>
 * 根据规则引擎模板获取保费金额
 */
@Component
@Slf4j
public class droolsUserEntityUtils {

    @Autowired
    private DroolsRulesConfigDao droolsRulesConfigDao;

    /**
     * 执行规则筛选
     * @param userEntity
     */
    public void executeRules(UserEntity userEntity) {
        List<DroolsRulesConfig> droolsRulesConfig = droolsRulesConfigDao.selectList(new QueryWrapper<DroolsRulesConfig>().eq("rule_name", "fix"));

        ObjectDataCompiler converter = new ObjectDataCompiler();
        String drlContent = StringUtils.EMPTY;
        try (InputStream dis = ResourceFactory.
                newClassPathResource("rules/prd/user-rules.drt", CharsetUtil.UTF_8)
                .getInputStream()) {
//            填充模板内容
            drlContent = converter.compile(droolsRulesConfig, dis);
            log.error("生成的规则内容:{}", drlContent);
        } catch (IOException e) {
            log.error("获取规则模板文件出错:{}", e.getMessage());
        }
        KieHelper helper = new KieHelper();
        helper.addContent(drlContent, ResourceType.DRL);
        KieSession ks = helper.build().newKieSession();
        ks.insert(userEntity);

        int allRules = ks.fireAllRules();
        log.error("成功执行{}条规则", allRules);
        ks.dispose();
    }

}
