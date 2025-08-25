package com.cjg.home.dto.request;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Builder
public class AlarmSaveRequestDto {

    private String userId;
    private String message;
    private String link;

}
