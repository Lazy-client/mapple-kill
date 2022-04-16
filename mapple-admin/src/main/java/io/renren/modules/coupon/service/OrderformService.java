package io.renren.modules.coupon.service;


import com.baomidou.mybatisplus.extension.service.IService;
import io.renren.common.utils.PageUtils;
import io.renren.modules.coupon.entity.OrderformEntity;

import java.util.Map;

/**
 * 订单信息表
 *
 * @author sicheng
 * @email sicheng_zhou@qq.com
 * @date 2022-03-13 15:23:08
 */
public interface OrderformService extends IService<OrderformEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

