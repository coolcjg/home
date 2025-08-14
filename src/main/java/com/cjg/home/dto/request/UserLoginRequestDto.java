package com.cjg.home.dto.request;


import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class UserLoginRequestDto {

    @NotBlank(message = "아이디를 입력하세요")
    private String userId;

    @NotBlank(message = "비밀번호를 입력하세요")
    private String password;
}
