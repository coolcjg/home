package com.cjg.home.dto.request;


import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class PostSaveRequestDto {

    @NotBlank(message = "아이디를 입력해주세요")
    private String userId;

    @NotBlank(message = "제목을 입력해주세요")
    private String title;

    private String content;

    @NotBlank(message = "공개 여부를 입력해주세요")
    private String open;
}
