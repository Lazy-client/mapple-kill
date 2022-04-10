package com.mapple.consume.service;

import com.mapple.common.utils.PageUtils;
import com.mapple.common.utils.result.CommonResult;
import com.mapple.consume.entity.MkOrder;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

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

    PageUtils queryPage(Map<String, Object> params, String userId);

    CommonResult payOrder(MkOrder order);

    CommonResult publicAccountBalance();

    List<String> getTimeoutRandomCodeList(long timeout, long currentTime);

    List<MkOrder> getBySnBatch(List<String> orderSnList);

    CommonResult sendDelay(String orderSn);

    CommonResult payOrderEnqueue(String orderId);

    void orderSaveBatch(List<MkOrder> orderList);

    int removeBatchBySnList(List<String> orderSnList);
}
