package com.cjg.home.dto.request;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class PostModifyRequestDto {

    @NotNull(message = "게시글 번호를 입력해주세요")
    private Long postId;

    @NotBlank(message = "제목을 입력해주세요")
    private String title;

    private String content;

    @Pattern(regexp = "^[YN]$", message = "값은 Y 또는 N만 허용됩니다")
    private String open;
}
