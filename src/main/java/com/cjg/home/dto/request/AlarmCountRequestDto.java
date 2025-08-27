package com.cjg.home.dto.request;


import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Builder
@Getter
@ToString
public class AlarmCountRequestDto {

    private String userId;
    private String checked;
}