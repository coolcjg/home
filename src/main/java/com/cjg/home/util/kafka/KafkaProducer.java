package com.cjg.home.util.kafka;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
@AllArgsConstructor
public class KafkaProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final Logger logger = LoggerFactory.getLogger(KafkaProducer.class);

    public void create(String topic, String message) {
        logger.info("topic : {}, message : {}", topic, message);
        CompletableFuture<SendResult<String,Object>> future = kafkaTemplate.send(topic, message);

        future.whenComplete((result, exception) -> {
            if(exception == null) {
                logger.info("Sent message : {} with offset : {}", message, result.getRecordMetadata().offset());
            }else {
                logger.info("Unable to send message : {}", exception.getMessage());
            }
        });


    }
}
