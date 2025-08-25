package com.cjg.home.util.kafka;

import com.cjg.home.dto.request.AlarmSaveRequestDto;
import com.cjg.home.service.AlarmService;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Log4j2
@RequiredArgsConstructor
public class KafkaConsumer {

    private final AlarmService alarmService;

    @KafkaListener(topics="test")
    public void listener(Object data) {
        ConsumerRecord consumerRecord = (ConsumerRecord)data;
        String value = (String)consumerRecord.value();

        JsonObject jo = JsonParser.parseString(value).getAsJsonObject();

        log.info("Kafka Consumer param {}", jo);

        AlarmSaveRequestDto dto = AlarmSaveRequestDto.builder()
                                    .userId(jo.get("userId").getAsString())
                                    .message(jo.get("message").getAsString())
                                    .link(jo.get("link").getAsString())
                                    .build();
        alarmService.save(dto);

    }
}
