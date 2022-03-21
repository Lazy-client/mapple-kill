package com.mapple.consume.service.impl;

import com.mapple.common.utils.CommonResult;
import com.mapple.consume.entity.MkOrder;
import com.mapple.consume.mapper.MkOrderMapper;
import com.mapple.consume.service.MkOrderService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 订单信息表 服务实现类
 * </p>
 *
 * @author Gelcon
 * @since 2022-03-19
 */
@Service
@Slf4j
public class MkOrderServiceImpl extends ServiceImpl<MkOrderMapper, MkOrder> implements MkOrderService {

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    @Override
    public CommonResult orderEnqueue(MkOrder order) {
        rocketMQTemplate.convertAndSend("MkOrder-Topic","Hello Springboot RocketMQ");
        log.info("消息发送成功");
        return CommonResult.ok();
    }
}
