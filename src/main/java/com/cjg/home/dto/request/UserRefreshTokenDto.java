package com.cjg.home.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRefreshTokenDto {

    @NotBlank(message = "userId를 입력하세요")
    String userId;

    String refreshToken;
}