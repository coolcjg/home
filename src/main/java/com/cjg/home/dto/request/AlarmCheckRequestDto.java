package com.cjg.home.dto.request;


import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Builder
@Getter
@ToString
public class AlarmCheckRequestDto {

    private String userId;
    private Long alarmId;
}
