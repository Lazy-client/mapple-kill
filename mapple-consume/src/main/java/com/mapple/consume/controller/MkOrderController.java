package com.mapple.consume.controller;


import com.mapple.common.utils.CommonResult;
import com.mapple.consume.entity.MkOrder;
import com.mapple.consume.service.MkOrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * <p>
 * 订单信息表 前端控制器
 * </p>
 *
 * @author Gelcon
 * @since 2022-03-19
 */
@RestController
@RequestMapping("/consume/mk-order")
public class MkOrderController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MkOrderController.class);

    @Resource
    private MkOrderService orderService;

    /**
     * 创建订单接口
     */
    @PostMapping("createOrder")
    public CommonResult createOrder(@RequestBody MkOrder order) {
        // 各种参数校验
        // 参数校验结束
        return orderService.orderEnqueue(order);
    }
}

