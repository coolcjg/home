package com.cjg.home.dto.response;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Builder
@Setter
@Getter
@ToString
public class CommentResponseDto {

    private Long commentId;
    private Long parentId;
    private Long postId;
    private String userId;
    private String name;
    private String regDate;
    private String content;
    private Character deleted;

}
