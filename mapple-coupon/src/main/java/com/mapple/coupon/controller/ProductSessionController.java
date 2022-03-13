package com.mapple.coupon.controller;

import java.util.Arrays;
import java.util.Map;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import com.mapple.coupon.entity.ProductSessionEntity;
import com.mapple.coupon.service.ProductSessionService;
import com.mapple.common.utils.PageUtils;
import com.mapple.common.utils.CommonResult;

import javax.annotation.Resource;



/**
 * 产品场次关联表
 *
 * @author sicheng
 * @email sicheng_zhou@qq.com
 * @date 2022-03-13 15:23:08
 */
@Api(tags = {"场次和产品关联"})
@RestController
@RequestMapping("coupon/productsession")
public class ProductSessionController {
    @Resource
    private ProductSessionService productSessionService;

    /**
     * 根据分页数据和场次id查询场次对应的商品
     * @param params
     * @return
     */
    @ApiOperation(value = "根据场次id查询场次对应的商品")
    @ApiImplicitParams(value =
            @ApiImplicitParam(name = "params",
                    value = "分页数据xxx与场次sessionId=xxx，封装在map中",
                    required = true,dataTypeClass =Map.class ))
    @GetMapping("/list")
    //@RequiresPermissions("coupon:productsession:list")
    public CommonResult list(@RequestParam Map<String, Object> params){
        PageUtils page = productSessionService.queryPage(params);

        return CommonResult.ok().put("page", page);
    }


    /**
     * 信息
     */
    @GetMapping("/info/{id}")
    //@RequiresPermissions("coupon:productsession:info")
    public CommonResult info(@PathVariable("id") String id){
		ProductSessionEntity productSession = productSessionService.getById(id);

        return CommonResult.ok().put("productSession", productSession);
    }

    /**
     * 保存
     */
    @PostMapping("/save")
    @ApiOperation(value = "选择场次后，在这个场次id下添加产品信息")
    @ApiImplicitParams(value =
    @ApiImplicitParam(name = "productSession",
            value = "参数包括场次id以及商品多个属性",
            required = true,dataTypeClass =ProductSessionEntity.class ))
    //@RequiresPermissions("coupon:productsession:save")
    public CommonResult save(@RequestBody ProductSessionEntity productSession){
		productSessionService.save(productSession);
        return CommonResult.ok();
    }

    /**
     * 修改
     */
    @PostMapping("/update")
    //@RequiresPermissions("coupon:productsession:update")
    public CommonResult update(@RequestBody ProductSessionEntity productSession){
		productSessionService.updateById(productSession);

        return CommonResult.ok();
    }

    /**
     * 删除
     */
    @PostMapping("/delete")
    //@RequiresPermissions("coupon:productsession:delete")
    public CommonResult delete(@RequestBody String[] ids){
		productSessionService.removeByIds(Arrays.asList(ids));

        return CommonResult.ok();
    }

}
