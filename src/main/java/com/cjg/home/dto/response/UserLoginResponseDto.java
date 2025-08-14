package com.cjg.home.dto.response;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UserLoginResponseDto {
    private String userId;
    private String name;
    private String accessToken;
    private String refreshToken;
}
