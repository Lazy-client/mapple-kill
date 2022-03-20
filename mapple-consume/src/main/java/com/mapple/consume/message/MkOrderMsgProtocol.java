package com.mapple.consume.message;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Map;

/**
 * @author snowalker
 * @version 1.0
 * @date 2019/6/12 11:55
 * @className OrderNofityProtocol
 * @desc 订单结果通知协议
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MkOrderMsgProtocol extends BaseMsg implements Serializable {

    private static final long serialVersionUID = 73717163386598209L;


    @ApiModelProperty(value = "场次id")
    private String sessionId;

    @ApiModelProperty(value = "产品id")
    private String productId;

    @ApiModelProperty(value = "用户id")
    private String userId;


    private Map<String, String> header;
    private Map<String, String> body;

    @Override
    public String encode() {
        // 组装消息协议头
        ImmutableMap.Builder headerBuilder = new ImmutableMap.Builder<String, String>()
                .put("version", this.getVersion())
                .put("topicName", MessageProtocolConst.MK_ORDER_TOPIC.getTopic());
        header = headerBuilder.build();

        body = new ImmutableMap.Builder<String, String>()
                .put("sessionId", this.getSessionId())
                .put("productId", this.getProductId())
                .put("userId", this.getUserId())
                .build();

        ImmutableMap<String, Object> map = new ImmutableMap.Builder<String, Object>()
                .put("header", header)
                .put("body", body)
                .build();

        // 返回序列化消息Json串
        String ret_string = null;
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            ret_string = objectMapper.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("ChargeOrderMsgProtocol消息序列化json异常", e);
        }
        return ret_string;
    }

    @Override
    public void decode(String msg) {
        Preconditions.checkNotNull(msg);
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode root = mapper.readTree(msg);
            // header
            this.setVersion(root.get("header").get("version").asText());
            this.setTopicName(root.get("header").get("topicName").asText());
            // body
            this.setSessionId(root.get("body").get("sessionId").asText());
            this.setProductId(root.get("body").get("productId").asText());
            this.setUserId(root.get("body").get("userId").asText());
        } catch (IOException e) {
            throw new RuntimeException("MkOrderMsgProtocol消息反序列化异常", e);
        }
    }

}
