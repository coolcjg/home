package com.cjg.home.dto.request;


import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class PostDeleteRequestDto {

    @NotNull(message = "게시글 번호를 입력해주세요")
    private Long[] postIds;
}
