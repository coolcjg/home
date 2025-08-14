package com.cjg.home.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Setter
@Getter
@Builder
@ToString
public class PostListResponseDto {
    private List<PostResponseDto> list;
    private int totalPage;
    private Long totalCount;

    private int pageNumber;
    private String nextPage;
    private String prevPage;
    private List<PageItem> pageList;

    private String searchType;
    private String searchText;
}
