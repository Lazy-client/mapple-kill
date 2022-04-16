package io.renren.modules.coupon.controller;


import io.renren.common.utils.PageUtils;
import io.renren.common.utils.R;
import io.renren.modules.coupon.entity.ProductSessionEntity;
import io.renren.modules.coupon.entity.vo.productSessionVo;
import io.renren.modules.coupon.entity.vo.productSessionVo_new;
import io.renren.modules.coupon.service.ProductSessionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;
import java.util.Map;


/**
 * 产品场次关联表
 *
 * @author sicheng
 * @email sicheng_zhou@qq.com
 * @date 2022-03-13 15:23:08
 */
@Api(tags = {"场次和产品关联"})
@RestController
@RequestMapping("/coupon/coupon/productsession")
@Slf4j
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
    public R list(@ApiParam(name = "productSessionVo",
            value = "分页数据xxx与场次sessionId=xxx，封装在map中",
            required = true) @RequestParam Map<String, Object> params) {
        PageUtils page = productSessionService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @ApiOperation(value = "根据")
    @GetMapping("/info/{id}")
    //@RequiresPermissions("coupon:productsession:info")
    public R info(@PathVariable("id") String id) {
        ProductSessionEntity productSession = productSessionService.getById(id);

        return R.ok().put("productSession", productSession);
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
    public R save(@Valid @ApiParam(name = "productSessionVo",
            value = "请传入：场次sessionId、商品对象productEntity、产品库存量totalCount",
            required = true) @RequestBody productSessionVo productSessionVo) {
        String result = productSessionService.saveProductSession(productSessionVo);
        if (!StringUtils.isEmpty(result)) {
            return R.ok().put("productSessionId", result);
        } else {
            return R.error("数据插入出错");
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
    public R save(@Valid @ApiParam(name = "productSessionVo_new",
            value = "请传入：场次sessionId、商品对象列表productList",
            required = true) @RequestBody productSessionVo_new productSessionVo_new) {
        List<String> strings = productSessionService.saveProductSession_new(productSessionVo_new);
        return R.ok().put("productSessionIdList", strings);
    }

    /**
     * 修改
     */
    @PostMapping("/update")
    //@RequiresPermissions("coupon:productsession:update")
    public R update(@RequestBody List<ProductSessionEntity> productSessionList) {
        productSessionService.updateBatchById(productSessionList);
        return R.ok();
    }

        protected Logger logger = LoggerFactory.getLogger(getClass());
    /**
     * 减库存
     */
    @ApiOperation(value = "扣减库存", notes = "扣减productCount数量的库存")
    @PostMapping("/deductStock/{productId}/{sessionId}")
    // @RequiresPermissions("coupon:productSession:deductStock")
    public int deductStock(@PathVariable String productId,
                           @PathVariable String sessionId) {
        logger.info("进入此处");
        return productSessionService.deductStock(productId, sessionId);
    }

    /**
     * 归还库存
     */
    @ApiOperation(value = "归还库存", notes = "归还productCount数量的库存")
    @PostMapping("/refundStock/{productId}/{sessionId}")
    // @RequiresPermissions("coupon:productSession:deductStock")
    public int refundStock(@PathVariable String productId,
                           @PathVariable String sessionId) {
        return productSessionService.refundStock(productId, sessionId);
    }

    /**
     * 删除
     */
    @PostMapping("/delete")
    @ApiOperation("delete关联")
    //@RequiresPermissions("coupon:productsession:delete")
    public R delete(@RequestBody String[] ids) {
        productSessionService.removeByIds(Arrays.asList(ids));
        return R.ok();
    }

}
