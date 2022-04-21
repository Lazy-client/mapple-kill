package com.mapple.consume.controller;

import com.mapple.common.config.interceptor.annotation.Login;
import com.mapple.common.utils.PageUtils;
import com.mapple.common.utils.jwt.JwtUtils;
import com.mapple.common.utils.result.CommonResult;
import com.mapple.common.vo.MkOrderPay;
import com.mapple.consume.entity.MkOrder;
import com.mapple.consume.service.AdminFeignService;
import com.mapple.consume.service.MkOrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class MkOrderController {
    @Resource
    private MkOrderService orderService;

    @Resource
    private AdminFeignService adminFeignService;

    /**
     * 创建订单接口
     */
    @ApiOperation(value = "订单进入消息队列", notes = "传入订单实体类")
    @PostMapping("createOrder")
    @Deprecated
    public CommonResult createOrder(@RequestBody MkOrder order) {
        // 各种参数校验
        // 参数校验结束
        return orderService.orderEnqueue(order);
    }

    /**
     * 发送延时消息
     */
    @ApiOperation(value = "延时消息进入队列", notes = "传入订单的SN标识")
    @PostMapping("sendDelay/{orderSn}")
    @Deprecated
    public CommonResult sendDelay(@PathVariable String orderSn) {
        // 各种参数校验
        // 参数校验结束
        return orderService.sendDelay(orderSn);
    }

    /**
     * 测试批量获取
     *
     * @param orderSnList
     * @return
     */
    @ApiOperation(value = "测试批量获取订单消息", notes = "传入订单Sn列表")
    @PostMapping("testGetSnBatch")
    @Deprecated
    public CommonResult testGetSnBatch(@RequestBody List<String> orderSnList) {
        // 各种参数校验
        // 参数校验结束
        List<MkOrder> bySnBatch = orderService.getBySnBatch(orderSnList);
        return CommonResult.ok().put("result", bySnBatch);
    }


    /*
     * 订单接口，供管理员使用
     */
    @ApiOperation(value = "管理员订单查询", notes = "status参数传入0，即为未支付订单，传入1即为已支付订单")
    @GetMapping("/listForAdmin")
    //    @RequiresPermissions("sys:order:list")
    public PageUtils listForAdmin(@RequestParam Map<String, Object> params) {
        return orderService.queryPageForAdmin(params);
    }

    /**
     * 订单列表,供用户使用
     * 0-未支付状态, 1-已支付
     */
    @ApiOperation(value = "订单查询", notes = "status参数传入0，即为未支付订单，传入1即为已支付订单")
    @GetMapping("/list")
    @Login
    public CommonResult list(@RequestParam Map<String, Object> params, @RequestHeader String token) {
        log.info("token==={}", token);
        String userId = JwtUtils.getUserIdByToken(token);
        log.info("userId===={}", userId);
        PageUtils page = orderService.queryPage(params, userId);

        return CommonResult.ok().put("page", page);
    }

    /**
     * 获取单个订单的信息
     */
    @ApiOperation(value = "单个订单详情界面", notes = "传入orderId，返回order对象")
    @GetMapping("/info/{orderId}")
    @Login
    public CommonResult info(@PathVariable String orderId) {
        return CommonResult.ok().put("order", orderService.getById(orderId));
    }

    /**
     * 支付接口
     * 支付：扣减库存，扣减个人账户余额，增加公共账户余额
     */
    @ApiOperation(value = "订单支付接口", notes = "传入orderId，设置支付状态为已支付")
    @PostMapping("/payOrder/{orderId}")
    @Login
    // @RequiresPermissions("sys:order:update")
    public CommonResult payOrder(@RequestBody MkOrderPay pay) {
        return adminFeignService.deductBalance(pay);
    }

//    /**
//     * 支付接口
//     */
//    @ApiOperation(value = "订单支付接口", notes = "传入orderId，设置支付状态为已支付")
//    @PostMapping("/payOrder/{orderId}")
//    // @RequiresPermissions("sys:order:update")
//    public CommonResult update(@PathVariable String orderId) {
//
//        return orderService.payOrderEnqueue(orderId);
//    }

    /**
     * 删除订单
     * 只允许已支付的订单被删除
     */
    @ApiOperation(value = "订单删除接口", notes = "只允许已经支付的订单被删除")
    @PostMapping("/delete")
    @Login
    // @RequiresPermissions("sys:order:delete")
    public CommonResult delete(@RequestBody List<String> orderIds) {
        orderService.removeByIds(orderIds);
        return CommonResult.ok("批量删除订单成功！");
    }

    @ApiOperation(value = "公共账户余额接口", notes = "直接从Redis中获取公共账户余额")
    @GetMapping("publicAccountBalance")
    @Deprecated
    public CommonResult publicAccountBalance() {
        return orderService.publicAccountBalance();
    }


    @ApiOperation(value = "定时删除订单接口")
    @GetMapping("/getTimeOrders")
    @Deprecated
    @Login
    public List<String> getTimeOrders(@RequestParam long timeout, @RequestParam long currentTime) {
        //TODO 删掉记录
        return orderService.getTimeoutRandomCodeList(timeout, currentTime);
    }
}

