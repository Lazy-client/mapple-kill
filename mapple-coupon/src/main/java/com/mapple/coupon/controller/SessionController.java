package com.mapple.coupon.controller;

import com.mapple.common.utils.PageUtils;
import com.mapple.common.utils.result.CommonResult;
import com.mapple.coupon.entity.SessionEntity;
import com.mapple.coupon.service.SessionService;

import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;


/**
 * 场次表
 *
 * @author sicheng
 * @email sicheng_zhou@qq.com
 * @date 2022-03-13 15:23:08
 */
@RestController
@RequestMapping("coupon/session")
public class SessionController {
    @Resource
    private SessionService sessionService;

    /**
     * 列表
     */
    @GetMapping("/list")
    //@RequiresPermissions("coupon:session:list")
    public CommonResult list(@RequestParam Map<String, Object> params){
        PageUtils page = sessionService.queryPage(params);

        return CommonResult.ok().put("page", page);
    }


    /**
     * 信息
     */
    @GetMapping("/info/{id}")
    //@RequiresPermissions("coupon:session:info")
    public CommonResult info(@PathVariable("id") String id){
		SessionEntity session = sessionService.getById(id);

        return CommonResult.ok().put("session", session);
    }

    /**
     * 保存
     */
    @PostMapping("/save")
    //@RequiresPermissions("coupon:session:save")
    public CommonResult save(@Valid @RequestBody SessionEntity session){
        String result = sessionService.saveSession(session);
        return CommonResult.ok().put("sessionId",result);
    }

    /**
     * 修改
     */
    @PostMapping("/update")
    //@RequiresPermissions("coupon:session:update")
    public CommonResult update(@RequestBody SessionEntity session){
		sessionService.updateById(session);

        return CommonResult.ok();
    }

    /**
     * 删除
     */
    @PostMapping("/delete")
    //@RequiresPermissions("coupon:session:delete")
    public CommonResult delete(@RequestBody String[] ids){
        List<String> list = Arrays.asList(ids);
        sessionService.delete(list);
        return CommonResult.ok();
    }

}
