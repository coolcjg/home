package com.cjg.home.dto.request;


import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CommentSaveRequestDto {

    private Long parentId;

    @NotNull(message="게시글 아이디를 입력해주세요")
    private Long postId;

    @NotNull(message="사용자 아이디를 입력해주세요")
    private String userId;

    @NotNull(message="내용을 입력해주세요")
    private String content;
}
