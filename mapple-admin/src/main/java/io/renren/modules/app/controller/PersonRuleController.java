package io.renren.modules.app.controller;

import io.renren.modules.app.entity.Person;
import io.renren.modules.app.entity.UserEntity;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author hxx
 * @date 2022/3/30 18:29
 */
@Api("规则筛选")
@RestController
@RequestMapping("/app/rule/userEntity")
public class PersonRuleController {
    @Autowired
    private KieContainer kieContainer;

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
    @PostMapping("one")
    public void filterUserByRules(@RequestBody UserEntity userEntity) {
        KieSession kSession = kieContainer.newKieSession();
        try {
            kSession.insert(userEntity);
            kSession.fireAllRules();
        } finally {
            kSession.dispose();
        }
    }

    @ApiOperation("（实操）筛选多个用户")
    @PostMapping("multi")
    public void filterManyUserByRules(@RequestBody List<UserEntity> userEntities) {
        KieSession kSession = kieContainer.newKieSession();
        try {
            for (UserEntity userEntity : userEntities) {
                kSession.insert(userEntity);
            }
            kSession.fireAllRules();
        } finally {
            kSession.dispose();
        }
    }

    @ApiOperation("修改规则")
    @PostMapping("updateRule")
    public void updateRule(@RequestBody List<UserEntity> userEntities) {

    }
}
