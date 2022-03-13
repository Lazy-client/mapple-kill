package com.mapple.coupon.controller;

import java.util.Arrays;
import java.util.Map;

import org.springframework.web.bind.annotation.*;

import com.mapple.coupon.entity.OrderformEntity;
import com.mapple.coupon.service.OrderformService;
import com.mapple.common.utils.PageUtils;
import com.mapple.common.utils.CommonResult;

import javax.annotation.Resource;



/**
 * 订单信息表
 *
 * @author sicheng
 * @email sicheng_zhou@qq.com
 * @date 2022-03-13 15:23:08
 */
@RestController
@RequestMapping("coupon/orderform")
public class OrderformController {
    @Resource
    private OrderformService orderformService;

    /**
     * 列表
     */
    @GetMapping("/list")
    //@RequiresPermissions("coupon:orderform:list")
    public CommonResult list(@RequestParam Map<String, Object> params){
        PageUtils page = orderformService.queryPage(params);

        return CommonResult.ok().put("page", page);
    }


    /**
     * 信息
     */
    @GetMapping("/info/{id}")
    //@RequiresPermissions("coupon:orderform:info")
    public CommonResult info(@PathVariable("id") String id){
		OrderformEntity orderform = orderformService.getById(id);

        return CommonResult.ok().put("orderform", orderform);
    }

    /**
     * 保存
     */
    @PostMapping("/save")
    //@RequiresPermissions("coupon:orderform:save")
    public CommonResult save(@RequestBody OrderformEntity orderform){
		orderformService.save(orderform);
        return CommonResult.ok();
    }

    /**
     * 修改
     */
    @PostMapping("/update")
    //@RequiresPermissions("coupon:orderform:update")
    public CommonResult update(@RequestBody OrderformEntity orderform){
		orderformService.updateById(orderform);

        return CommonResult.ok();
    }

    /**
     * 删除
     */
    @PostMapping("/delete")
    //@RequiresPermissions("coupon:orderform:delete")
    public CommonResult delete(@RequestBody String[] ids){
		orderformService.removeByIds(Arrays.asList(ids));

        return CommonResult.ok();
    }

}
