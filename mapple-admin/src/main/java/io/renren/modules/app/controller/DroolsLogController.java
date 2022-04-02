package io.renren.modules.app.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.renren.common.utils.PageUtils;
import io.renren.common.utils.R;
import io.renren.modules.app.dao.DroolsLogDao;
import io.renren.modules.app.entity.drools.DroolsLog;
import io.renren.modules.app.form.RegisterForm;
import io.renren.modules.app.service.DroolsLogService;
import io.renren.modules.app.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.models.auth.In;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @author hxx
 * @date 2022/4/2 16:15
 */
@RestController
@RequestMapping("/app/log")
@Api("drools日志管理接口")
public class DroolsLogController {
    @Autowired
    private DroolsLogDao droolsLogDao;

    @GetMapping("/list")
    @ApiOperation("获取日志信息")
    public R ruleLogsByPage(@ApiParam(name = "页数",
            value = "page必传，多条件查询要添加用户名比如user1、规则id",
            required = true)@RequestParam Integer nowPage
            ,@RequestParam(required = false) String username
            ,@RequestParam(required = false) String ruleId){
        Page<DroolsLog> page= new Page<>(nowPage,100);
        LambdaQueryWrapper<DroolsLog> queryWrapper = new LambdaQueryWrapper<>();

        //是否置顶进行排序 创建日期排序
        queryWrapper.orderByDesc(DroolsLog::getGmtCreate);
        if (!StringUtils.isEmpty(username)){
            queryWrapper.eq(DroolsLog::getUsername,username);
        }
        if (!StringUtils.isEmpty(ruleId)){
            queryWrapper.eq(DroolsLog::getRuleId,ruleId);
        }
        Page<DroolsLog> articlePage = droolsLogDao.selectPage(page, queryWrapper);

        List<DroolsLog> records = articlePage.getRecords();

        PageUtils pageUtils = new PageUtils(records, (int) articlePage.getTotal(), 100, nowPage);
        return R.ok().put("page", pageUtils);
    }
}
