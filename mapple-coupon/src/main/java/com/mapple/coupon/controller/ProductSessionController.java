package com.mapple.coupon.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.mapple.coupon.entity.vo.productSessionVo;
import com.mapple.coupon.entity.vo.productSessionVo_new;
import io.swagger.annotations.*;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import com.mapple.coupon.entity.ProductSessionEntity;
import com.mapple.coupon.service.ProductSessionService;
import com.mapple.common.utils.PageUtils;
import com.mapple.common.utils.CommonResult;

import javax.annotation.Resource;
import javax.validation.Valid;


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
     *
     * @param params
     * @return
     */
    @ApiOperation(value = "根据场次id查询场次对应的商品")
//    @ApiImplicitParams(value =
//            @ApiImplicitParam(name = "params",
//                    value = "分页数据xxx与场次sessionId=xxx，封装在map中",
//                    required = true,
//            dataType = "Map<String, Object>"))
    @GetMapping("/list")
    //@RequiresPermissions("coupon:productsession:list")
    public CommonResult list(@ApiParam(name = "productSessionVo",
            value = "请传入：场次sessionId、商品对象productEntity、产品库存量totalCount",
            required = true) @RequestParam Map<String, Object> params) {
        PageUtils page = productSessionService.queryPage(params);

        return CommonResult.ok().put("page", page);
    }


    /**
     * 信息
     */
    @ApiOperation(value = "根据")
    @GetMapping("/info/{id}")
    //@RequiresPermissions("coupon:productsession:info")
    public CommonResult info(@PathVariable("id") String id) {
        ProductSessionEntity productSession = productSessionService.getById(id);

        return CommonResult.ok().put("productSession", productSession);
    }

    /**
     * 保存
     */
    @PostMapping("/save")
    @ApiOperation(value = "选择场次后，在这个场次id下添加产品信息")
//    @ApiImplicitParams(value =
//    @ApiImplicitParam(name = "productSessionVo",
//            value = "请传入：场次sessionId、商品对象product、产品库存量totalCount",
//            required = true,
//            paramType = "body"))
    //@RequiresPermissions("coupon:productsession:save")
    public CommonResult save(@Valid @ApiParam(name = "productSessionVo",
            value = "请传入：场次sessionId、商品对象productEntity、产品库存量totalCount",
            required = true) @RequestBody productSessionVo productSessionVo) {
        String result = productSessionService.saveProductSession(productSessionVo);
        if (!StringUtils.isEmpty(result)) {
            return CommonResult.ok().put("productSessionId", result);
        } else {
            return CommonResult.error("数据插入出错");
        }
    }

    /**
     * 保存
     */
    @PostMapping("/saveNew")
    @ApiOperation(value = "选择场次后，在这个场次id下添加产品信息")
//    @ApiImplicitParams(value =
//    @ApiImplicitParam(name = "productSessionVo",
//            value = "请传入：场次sessionId、商品对象product、产品库存量totalCount",
//            required = true,
//            paramType = "body"))
    //@RequiresPermissions("coupon:productsession:save")
    public CommonResult save(@Valid @ApiParam(name = "productSessionVo_new",
            value = "请传入：场次sessionId、商品对象列表productList",
            required = true) @RequestBody productSessionVo_new productSessionVo_new) {
        List<String> strings = productSessionService.saveProductSession_new(productSessionVo_new);
        return CommonResult.ok().put("productSessionIdList", strings);
    }

    /**
     * 修改
     */
    @PostMapping("/update")
    //@RequiresPermissions("coupon:productsession:update")
    public CommonResult update(@RequestBody ProductSessionEntity productSession) {
        productSessionService.updateById(productSession);
        return CommonResult.ok();
    }

    /**
     * 删除
     */
    @PostMapping("/delete")
    //@RequiresPermissions("coupon:productsession:delete")
    public CommonResult delete(@RequestBody String[] ids) {
        productSessionService.removeByIds(Arrays.asList(ids));

        return CommonResult.ok();
    }

}
