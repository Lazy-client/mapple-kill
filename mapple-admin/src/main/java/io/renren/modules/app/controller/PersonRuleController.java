package io.renren.modules.app.controller;

import io.renren.common.utils.R;
import io.renren.modules.app.entity.drools.DroolsRulesConfig;
import io.renren.modules.app.entity.Person;
import io.renren.modules.app.entity.UserEntity;
import io.renren.modules.app.service.DroolsRulesConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * @author hxx
 * @date 2022/3/30 18:29
 */
@Api("规则筛选")
@RestController
@RequestMapping("/app/rules/user")
public class PersonRuleController {
    @Autowired
    private KieContainer kieContainer;

    @Autowired
    private DroolsRulesConfigService droolsRulesConfigService;

    @ApiOperation("测试筛选一个用户")
    @PostMapping("one")
    public void fireAllRules4One(@RequestBody Person person) {
        KieSession kSession = kieContainer.newKieSession();
        try {
            kSession.insert(person);
            kSession.fireAllRules();
        } finally {
            kSession.dispose();
        }
    }

    @ApiOperation("测试筛选多个用户")
    @PostMapping("list")
    public void fireAllRules4List(@RequestBody List<Person> persons) {
        KieSession kSession = kieContainer.newKieSession();
        try {
            for (Person person : persons) {
                kSession.insert(person);
            }
            kSession.fireAllRules();
        } finally {
            kSession.dispose();
        }
    }

    @ApiOperation("（实操）筛选一个或多个用户")
    @PostMapping("multiUser")
    public R filterManyUserByRules(@RequestBody List<UserEntity> userEntities) {
        String res = droolsRulesConfigService.filterUsers(userEntities);
        return R.ok().put("result",res);
    }

    @ApiOperation("修改筛选规则")
    @PostMapping("update")
    public R updateRules(@Valid @RequestBody DroolsRulesConfig droolsRulesConfig) {
        droolsRulesConfigService.updateRules(droolsRulesConfig);
        return R.ok();
    }
}
