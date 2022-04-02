package io.renren.modules.app.controller;

import io.renren.common.utils.R;
import io.renren.modules.app.entity.Person;
import io.renren.modules.app.entity.UserEntity;
import io.renren.modules.app.entity.drools.DroolsRulesConfig;
import io.renren.modules.app.service.DroolsRulesConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
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

    @ApiOperation("（没用）测试筛选一个用户")
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

    @ApiOperation("（没用）测试筛选多个用户")
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

    @ApiOperation("（可用）筛选一个或多个用户user")
    @PostMapping("multiUser")
    public R filterManyUserByRules(@RequestBody @ApiParam(name = "List<UserEntity>",
            value = "传入user用户，需要是列表，因为支持多个用户传入",
            required = true)List<UserEntity> userEntities) {
        String res = droolsRulesConfigService.filterUsers(userEntities);
        return R.ok().put("result",res);
    }

    @ApiOperation("（可用）修改筛选规则")
    @PostMapping("update")
    public R updateRules(@Valid @RequestBody @ApiParam(name = "DroolsRulesConfig",
            value = "请传入id=1，年龄上下界，是否需要工作，是否欠款预期，是否失信（三个是否需要传入true或false的字符串），存款金额下界，",
            required = true)DroolsRulesConfig droolsRulesConfig) {
        droolsRulesConfigService.updateRules(droolsRulesConfig);
        return R.ok();
    }

    @ApiOperation("（可用）查询筛选规则")
    @GetMapping("selectRule")
    public R selectRules(@RequestParam @ApiParam(
            value = "请传入id=1,返回的三个Boolean如果为空表示：此规则字段当前没开启，有无工作都可，false表示必须为有工作才能通过筛选",
            required = true)Integer ruleId) {
        DroolsRulesConfig config = droolsRulesConfigService.getById(ruleId);
        return R.ok().put("rule",config);
    }


}
