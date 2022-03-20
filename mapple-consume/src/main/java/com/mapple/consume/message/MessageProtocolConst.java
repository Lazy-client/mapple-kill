package com.mapple.consume.message;

/**
 * @author Gelcon
 * @date 2022年3月19日22:35:04
 * @desc 消息协议常量
 */
public enum MessageProtocolConst {

    /**SECKILL_CHARGE_ORDER_TOPIC 秒杀下单消息协议*/
    MK_ORDER_TOPIC("MK_ORDER_TOPIC", "MK_ORDER_PRODUCER", "MK_ORDER_CONSUMER", "秒杀下单消息协议"),
    ;
    /**消息主题*/
    private String topic;
    /**生产者组*/
    private String producerGroup;
    /**消费者组*/
    private String consumerGroup;
    /**消息描述*/
    private String desc;

    MessageProtocolConst(String topic, String producerGroup, String consumerGroup, String desc) {
        this.topic = topic;
        this.producerGroup = producerGroup;
        this.consumerGroup = consumerGroup;
        this.desc = desc;
    }

    public String getTopic() {
        return topic;
    }

    public String getProducerGroup() {
        return producerGroup;
    }

    public String getDesc() {
        return desc;
    }

    public String getConsumerGroup() {
        return consumerGroup;
    }}
