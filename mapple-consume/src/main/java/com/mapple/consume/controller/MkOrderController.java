package com.mapple.consume.controller;


import com.mapple.common.utils.PageUtils;
import com.mapple.common.utils.result.CommonResult;
import com.mapple.consume.entity.MkOrder;
import com.mapple.consume.service.MkOrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 订单信息表 前端控制器
 * </p>
 *
 * @author Gelcon
 * @since 2022-03-19
 */
@Api(tags = {"订单接口"})
@RestController
@RequestMapping("/mk-order")
public class MkOrderController {


    @Resource
    private MkOrderService orderService;

//    /**
//     * 创建订单接口
//     */
//    @ApiOperation(value = "订单进入消息队列", notes = "传入订单实体类")
//    @PostMapping("createOrder")
//    public CommonResult createOrder(@RequestBody MkOrder order) {
//        // 各种参数校验
//        // 参数校验结束
//        return orderService.orderEnqueue(order);
//    }

    /**
     * 订单列表
     * 0-未支付状态, 1-已支付
     */
    @ApiOperation(value = "订单查询", notes = "status参数传入0，即为未支付订单，传入1即为已支付订单")
    @GetMapping("/list")
//    @RequiresPermissions("sys:order:list")
    public CommonResult list(@RequestParam Map<String, Object> params) {

        PageUtils page = orderService.queryPage(params);
        return CommonResult.ok().put("page", page);
    }

    /**
     * 获取单个订单的信息
     */
    @ApiOperation(value = "单个订单详情界面", notes = "传入orderId，返回order对象")
    @GetMapping("/info/{orderId}")
    public CommonResult info(@PathVariable String orderId) {
        return CommonResult.ok().put("order", orderService.getById(orderId));
    }

    /**
     * 支付接口
     */
    @ApiOperation(value = "订单支付接口", notes = "传入orderId，设置支付状态为已支付")
    @PostMapping("/payOrder/{orderId}")
    // @RequiresPermissions("sys:order:update")
    public CommonResult update(@PathVariable String orderId) {
        MkOrder order = orderService.getById(orderId);
        if (order.getStatus() == 1)
            return CommonResult.error("订单已支付，请勿重复操作！");

        return orderService.payOrder(order);
    }

    /**
     * 删除订单
     * 只允许已支付的订单被删除
     */
    @ApiOperation(value = "订单删除接口", notes = "只允许已经支付的订单被删除")
    @PostMapping("/delete")
    // @RequiresPermissions("sys:order:delete")
    public CommonResult delete(@RequestBody List<String> orderIds) {
        orderService.removeByIds(orderIds);
        return CommonResult.ok("批量删除订单成功！");
    }
}

