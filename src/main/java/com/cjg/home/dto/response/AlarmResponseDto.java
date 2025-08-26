package com.cjg.home.dto.response;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@ToString
public class AlarmResponseDto {
    private long alarmId;
    private String checked;
    private String link;
    private String message;
    private String regDate;
    private String userId;
}
