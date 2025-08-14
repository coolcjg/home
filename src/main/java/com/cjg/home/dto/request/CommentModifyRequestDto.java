package com.cjg.home.dto.request;


import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CommentModifyRequestDto {

    @NotNull(message="댓글 아이디를 입력해주세요")
    private Long commentId;

    @NotNull(message="내용을 입력해주세요")
    private String content;
}
