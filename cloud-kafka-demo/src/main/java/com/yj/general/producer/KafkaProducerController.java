package com.yj.general.producer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: KafkaProducer
 * @Description: kafkaProducer
 * @Author: yanjianxun
 * @Date 2021/11/8
 * @Version 1.0
 */
@RestController
public class KafkaProducerController {
    private final static String TOPIC_NAME = "topic"; //topic的名称

    @Autowired
    private KafkaTemplate kafkaTemplate;

    @PutMapping("/send")
    public void send() {
        ListenableFuture<SendResult<String, String>> future = kafkaTemplate.send(TOPIC_NAME, "key", "test message send~");
        future.addCallback(new ListenableFutureCallback<SendResult<String, String>>() {
            @Override
            public void onFailure(Throwable throwable) {
                System.out.println("消息发送失败!");
            }

            @Override
            public void onSuccess(SendResult<String, String> stringStringSendResult) {
                System.out.println("消息发送成功!");
            }
        });
    }
}
