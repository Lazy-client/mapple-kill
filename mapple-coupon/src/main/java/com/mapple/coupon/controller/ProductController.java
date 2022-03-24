package com.mapple.coupon.controller;

import java.util.Arrays;
import java.util.Map;

import com.mapple.common.exception.RRException;
import org.springframework.web.bind.annotation.*;

import com.mapple.coupon.entity.ProductEntity;
import com.mapple.coupon.service.ProductService;
import com.mapple.common.utils.PageUtils;
import com.mapple.common.utils.result.CommonResult;

import javax.annotation.Resource;
import javax.validation.Valid;


/**
 * 产品信息表
 *
 * @author sicheng
 * @email sicheng_zhou@qq.com
 * @date 2022-03-13 15:23:08
 */
@RestController
@RequestMapping("coupon/product")
public class ProductController {
    @Resource
    private ProductService productService;

    /**
     * 列表
     */
    @GetMapping("/list")
    //@RequiresPermissions("coupon:product:list")
    public CommonResult list(@RequestParam Map<String, Object> params){
        PageUtils page = productService.queryPage(params);

        return CommonResult.ok().put("page", page);
    }


    /**
     * 信息
     */
    @GetMapping("/info/{id}")
    //@RequiresPermissions("coupon:product:info")
    public CommonResult info(@PathVariable("id") String id){
		ProductEntity product = productService.getById(id);

        return CommonResult.ok().put("product", product);
    }

    /**
     * 保存
     */
    @PostMapping("/save")
    //@RequiresPermissions("coupon:product:save")
    public CommonResult save(@Valid @RequestBody ProductEntity product){
        productService.saveProduct(product);
        return CommonResult.ok();
    }

    /**
     * 修改
     */
    @PostMapping("/update")
    //@RequiresPermissions("coupon:product:update")
    public CommonResult update(@RequestBody ProductEntity product){
		productService.updateById(product);

        return CommonResult.ok();
    }

    /**
     * 删除
     */
    @PostMapping("/delete")
    //@RequiresPermissions("coupon:product:delete")
    public CommonResult delete(@RequestBody String[] ids){
		productService.removeByIds(Arrays.asList(ids));

        return CommonResult.ok();
    }

}
