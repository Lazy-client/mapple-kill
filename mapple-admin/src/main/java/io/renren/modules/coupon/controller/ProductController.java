package io.renren.modules.coupon.controller;


import io.renren.common.utils.PageUtils;
import io.renren.common.utils.R;
import io.renren.modules.coupon.entity.ProductEntity;
import io.renren.modules.coupon.entity.vo.productSessionVo_Skus;
import io.renren.modules.coupon.service.ProductService;
import io.renren.modules.coupon.service.SessionService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;


/**
 * 产品信息表
 *
 * @author sicheng
 * @email sicheng_zhou@qq.com
 * @date 2022-03-13 15:23:08
 */
@RestController
@RequestMapping("/coupon/coupon/product")
public class ProductController {
    @Resource
    private ProductService productService;

    /**
     * 列表
     */
    @GetMapping("/list")
//    @SentinelResource(value = "couponlist"
//            ,blockHandlerClass = sentinelHandler.class
//            ,blockHandler = "handlerExceptionSentinel")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = productService.queryPage(params);
        return R.ok().put("page", page);
    }

    /**
     * 信息
     */
    @GetMapping("/info/{id}")
    //@RequiresPermissions("coupon:product:info")
    public R info(@PathVariable("id") String id) {
        ProductEntity product = productService.getById(id);

        return R.ok().put("product", product);
    }

    /**
     * 保存产品信息
     */
    @ApiOperation(value = "添加产品信息")
    @PostMapping("/save")
    public R save(@Valid @ApiParam(name = "productSessionVo_new",
            value = "请传入productEntity中的信息，注意产品存期的格式",
            required = true) @RequestBody ProductEntity product) {
        productService.saveProduct(product);
        return R.ok();
    }

    @Autowired
    private SessionService sessionService;

    /**
     * 修改
     */
    @PostMapping("/update")
    @ApiOperation("曹尼玛")
    //@RequiresPermissions("coupon:product:update")
    public R update(@Valid @ApiParam(name = "productSessionVo_Skus",
            value = "请传入sessionId和productId,然后加上要修改的字段，注意一次修改一个产品信息，修改产品字段或者修改场次和产品关联的两个字段：秒杀价和库存，都可以在这个接口传入",
            required = true) @RequestBody List<productSessionVo_Skus> productSessionVo_Skus_list) {
        String sessionId = productSessionVo_Skus_list.get(0).getSessionId();
//        productSessionVo_Skus_list.get(0).getSessionName()
        ArrayList<String> ids = new ArrayList<>();
        ids.add(sessionId);
        sessionService.delete(ids);
//        productSessionVo_Skus_list.
        return R.ok();
    }

    /**
     * 删除
     */
    @PostMapping("/delete")
    //@RequiresPermissions("coupon:product:delete")
    public R delete(@RequestBody String[] ids) {
        productService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
