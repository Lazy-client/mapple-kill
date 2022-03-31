package io.renren.modules.app.controller;

import io.renren.common.utils.R;
import io.renren.modules.app.entity.DroolsRulesConfig;
import io.renren.modules.app.entity.Person;
import io.renren.modules.app.entity.UserEntity;
import io.renren.modules.app.service.DroolsRulesConfigService;
import io.renren.modules.app.utils.droolsUserEntityUtils;
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
    private droolsUserEntityUtils droolsUserEntityUtils;

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

    @ApiOperation("（实操）筛选一个用户")
    @PostMapping ("oneUser")
    public void filterUserByRules(@RequestBody UserEntity userEntity) {
//        KieSession kSession = kieContainer.newKieSession();
//        try {
//            kSession.insert(userEntity);
//            kSession.fireAllRules();
//        } finally {
//            kSession.dispose();
//        }

        droolsUserEntityUtils.executeRules(userEntity);
        System.out.println("完成！");
    }
//
//    @ApiOperation("（实操）筛选多个用户")
//    @PostMapping("multiUser")
//    public void filterManyUserByRules(@RequestBody List<UserEntity> userEntities) {
//        KieSession kSession = kieContainer.newKieSession();
//        try {
//            for (UserEntity userEntity : userEntities) {
//                kSession.insert(userEntity);
//            }
//            kSession.fireAllRules();
//        } finally {
//            kSession.dispose();
//        }
//    }
//
//    @ApiOperation("修改规则")
//    @PostMapping("updateRule")
//    public void updateRule(@RequestBody List<UserEntity> userEntities) {
//
//    }

    @ApiOperation("修改筛选规则")
    @PostMapping ("update")
    public R updateRules(@Valid @RequestBody DroolsRulesConfig droolsRulesConfig) {
        //修改规则
        if (droolsRulesConfigService.updateById(droolsRulesConfig)) {
            return R.ok();
        }
        return R.error();
    }
}
