package com.mapple.consume.service.impl;

import com.alibaba.fastjson.JSON;
import com.mapple.common.utils.CommonResult;
import com.mapple.consume.entity.MkOrder;
import com.mapple.consume.entity.dto.MkOrderResponse;
import com.mapple.consume.mapper.MkOrderMapper;
import com.mapple.consume.message.*;
import com.mapple.consume.service.MkOrderService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.common.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;

/**
 * <p>
 * 订单信息表 服务实现类
 * </p>
 *
 * @author Gelcon
 * @since 2022-03-19
 */
@Service
public class MkOrderServiceImpl extends ServiceImpl<MkOrderMapper, MkOrder> implements MkOrderService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MkOrderServiceImpl.class);

    @Resource
    MkOrderProducer orderProducer;

    @Override
    public CommonResult orderEnqueue(MkOrder order) {

        MkOrderMsgProtocol orderMsgProtocol = new MkOrderMsgProtocol();
        orderMsgProtocol.setSessionId(order.getSessionId());
        orderMsgProtocol.setUserId(order.getUserId());
        orderMsgProtocol.setProductId(order.getProductId());
        String msgBody = orderMsgProtocol.encode();
        LOGGER.info("秒杀订单入队,消息协议={}", msgBody);

        DefaultMQProducer mqProducer = orderProducer.getProducer();
        // 组装RocketMQ消息体
        Message message = new Message(MessageProtocolConst.MK_ORDER_TOPIC.getTopic(), msgBody.getBytes());
        try {
            // 消息发送
            SendResult sendResult = mqProducer.send(message);
            // TODO 判断SendStatus
            if (sendResult == null) {
                LOGGER.error("userId={},秒杀订单消息投递失败,下单失败.msgBody={},sendResult=null", order.getUserId(), msgBody);
                return CommonResult.error(CodeMsg.BIZ_ERROR.getMsg());
            }
            if (sendResult.getSendStatus() != SendStatus.SEND_OK) {
                LOGGER.error("userId={},秒杀订单消息投递失败,下单失败.msgBody={},sendResult=null", order.getUserId(), msgBody);
                return CommonResult.error(CodeMsg.BIZ_ERROR.getMsg());
            }
            MkOrderResponse orderResponse = new MkOrderResponse();
            BeanUtils.copyProperties(orderMsgProtocol, orderResponse);
            LOGGER.info("userId={},秒杀订单消息投递成功,订单入队.出参chargeOrderResponse={},sendResult={}", order.getUserId(), orderResponse.toString(), JSON.toJSONString(sendResult));
            return CommonResult.ok().put(CodeMsg.ORDER_INLINE.getCode(), orderResponse);
        } catch (Exception e) {
            int sendRetryTimes = mqProducer.getRetryTimesWhenSendFailed();
            LOGGER.error("userId={},sendRetryTimes={},秒杀订单消息投递异常,下单失败.msgBody={},e={}", order.getUserId(), sendRetryTimes, msgBody, LogExceptionWapper.getStackTrace(e));
        }
        return CommonResult.error(CodeMsg.BIZ_ERROR.getMsg());
    }
}
