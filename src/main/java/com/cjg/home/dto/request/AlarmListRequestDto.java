package com.cjg.home.dto.request;


import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Builder
@Getter
@ToString
public class AlarmListRequestDto {

    private String userId;
    private Integer pageNumber;
    private Integer pageSize;
}
