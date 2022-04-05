/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 * <p>
 * https://www.renren.io
 * <p>
 * 版权所有，侵权必究！
 */

package io.renren.modules.sys.controller;


import io.renren.common.annotation.SysLog;
import io.renren.common.utils.PageUtils;
import io.renren.common.utils.R;
import io.renren.common.validator.ValidatorUtils;
import io.renren.modules.sys.entity.SysConfigEntity;
import io.renren.modules.sys.service.SysConfigService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.redisson.api.RBloomFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;

/**
 * 系统配置信息
 *
 * @author Mark sunlightcs@gmail.com
 */
@RestController
@RequestMapping("/sys/config")
public class SysConfigController extends AbstractController {
    @Autowired
    private SysConfigService sysConfigService;

    /**
     * 所有配置列表
     */
    @GetMapping("/list")
//	@RequiresPermissions("sys:com.mapper.gateway.config:list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = sysConfigService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 配置信息
     */
    @GetMapping("/info/{id}")
    @RequiresPermissions("sys:com.mapper.gateway.config:info")
    public R info(@PathVariable("id") Long id) {
        SysConfigEntity config = sysConfigService.getById(id);

        return R.ok().put("com.mapper.gateway.config", config);
    }

    /**
     * 保存配置
     */
    @SysLog("保存配置")
    @PostMapping("/save")
    @RequiresPermissions("sys:com.mapper.gateway.config:save")
    public R save(@RequestBody SysConfigEntity config) {
        ValidatorUtils.validateEntity(config);

        sysConfigService.saveConfig(config);

        return R.ok();
    }

	@Resource
	private RBloomFilter<String> userBloomFilter;
    /**
     * 修改配置
     */
    @SysLog("修改配置")
    @PostMapping("/update")
    @RequiresPermissions("sys:com.mapper.gateway.config:update")
    public R update(@RequestBody SysConfigEntity config) {
        ValidatorUtils.validateEntity(config);

        //关闭初筛时，关闭布隆过滤器
        logger.info("config.getV = {}", config.getParamValue());
        if (config.getId() == 2L && !config.getParamValue().equals("true")) {
			userBloomFilter.delete();
        }else if (config.getId() == 2L && config.getParamValue().equals("true")) {
			userBloomFilter.tryInit(10000000L, 0.01);
		}
        sysConfigService.update(config);

        return R.ok();
    }

    /**
     * 删除配置
     */
    @SysLog("删除配置")
    @PostMapping("/delete")
    @RequiresPermissions("sys:com.mapper.gateway.config:delete")
    public R delete(@RequestBody Long[] ids) {
        sysConfigService.deleteBatch(ids);

        return R.ok();
    }

}
