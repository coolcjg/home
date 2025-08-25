package com.cjg.home.dto.response;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Builder
@Setter
@Getter
@ToString
public class SubscribeResponseDto {

    private String userId;
    private String targetUserId;
    private String regDate;

}
