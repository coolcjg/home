package com.cjg.home.dto.request;


import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class UserModifyRequestDto {

    @NotBlank(message = "아이디를 입력하세요")
    private String userId;

    @NotBlank(message = "이름를 입력하세요")
    private String name;

    @NotBlank(message = "패스워드를 입력하세요")
    private String password;

    private MultipartFile image;
}
