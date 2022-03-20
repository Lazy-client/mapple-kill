package com.mapple.consume.service;

import com.mapple.common.utils.CommonResult;
import com.mapple.consume.entity.MkOrder;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 订单信息表 服务类
 * </p>
 *
 * @author Gelcon
 * @since 2022-03-19
 */
public interface MkOrderService extends IService<MkOrder> {

    CommonResult orderEnqueue(MkOrder order);
}
