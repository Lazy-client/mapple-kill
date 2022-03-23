package com.mapple.coupon.controller;

import java.util.Arrays;
import java.util.Map;

import org.springframework.web.bind.annotation.*;

import com.mapple.coupon.entity.StockEntity;
import com.mapple.coupon.service.StockService;
import com.mapple.common.utils.PageUtils;
import com.mapple.common.utils.result.CommonResult;

import javax.annotation.Resource;



/**
 * 库存信息表
 *
 * @author sicheng
 * @email sicheng_zhou@qq.com
 * @date 2022-03-13 15:23:08
 */
@RestController
@RequestMapping("coupon/stock")
public class StockController {
    @Resource
    private StockService stockService;

    /**
     * 列表
     */
    @GetMapping("/list")
    //@RequiresPermissions("coupon:stock:list")
    public CommonResult list(@RequestParam Map<String, Object> params){
        PageUtils page = stockService.queryPage(params);

        return CommonResult.ok().put("page", page);
    }


    /**
     * 信息
     */
    @GetMapping("/info/{id}")
    //@RequiresPermissions("coupon:stock:info")
    public CommonResult info(@PathVariable("id") String id){
		StockEntity stock = stockService.getById(id);

        return CommonResult.ok().put("stock", stock);
    }

    /**
     * 保存
     */
    @PostMapping("/save")
    //@RequiresPermissions("coupon:stock:save")
    public CommonResult save(@RequestBody StockEntity stock){
		stockService.save(stock);
        return CommonResult.ok();
    }

    /**
     * 修改
     */
    @PostMapping("/update")
    //@RequiresPermissions("coupon:stock:update")
    public CommonResult update(@RequestBody StockEntity stock){
		stockService.updateById(stock);

        return CommonResult.ok();
    }

    /**
     * 删除
     */
    @PostMapping("/delete")
    //@RequiresPermissions("coupon:stock:delete")
    public CommonResult delete(@RequestBody String[] ids){
		stockService.removeByIds(Arrays.asList(ids));

        return CommonResult.ok();
    }

}
