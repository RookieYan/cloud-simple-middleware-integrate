package com.yj.general.consumer;

import org.springframework.stereotype.Component;

/**
 * @ClassName: KafkaConsumer
 * @Description: kafkaConsumer
 * @Author: yanjianxun
 * @Date 2021/11/8
 * @Version 1.0
 */
@Component
public class KafkaConsumer {

    /**
     * 这里的入参 Acknowledgment ack 需要 spring-kafka 版本为2.6.6及以上, 之前版本会报错.
     * https://github.com/spring-projects/spring-framework/issues/26389
     * @param record
     * @param ack
     */
//    @KafkaListener(topics = "#{kafkaTopicName}", groupId = "#{topicGroupId}")
//    public void listenConsumerGroup(ConsumerRecord<String, String> record, Acknowledgment ack) {
//        String value = record.value();
//        System.out.println(value);
//        System.out.println(record);
//        //手动提交offset
//        ack.acknowledge();
//    }
}
