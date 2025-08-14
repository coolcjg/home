package com.cjg.home.dto.response;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@Builder
@ToString
public class PostResponseDto {
    private long postId;
    private String userId;
    private String name;
    private String image;
    private String title;
    private String content;
    private char open;
    private int viewCnt;
    private String regDate;
    private String modDate;

    private List<CommentResponseDto> commentResponseDtoList;
}
