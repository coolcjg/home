package com.cjg.home.util.kafka;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.extern.log4j.Log4j2;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Log4j2
public class KafkaConsumer {

    @KafkaListener(topics="test")
    public void listener(Object data) {
        ConsumerRecord consumerRecord = (ConsumerRecord)data;
        String value = (String)consumerRecord.value();

        JsonObject jo = JsonParser.parseString(value).getAsJsonObject();
        log.info("param {}", jo);

    }
}
