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
public class MovieListResponseDto {
    private List<MovieResponseDto> list;
    private List<Integer> pageList;

    private long totCnt;
    private long totPage;
    private int curPage;
    private int itemPerPage;

    private String movieNm;
    private String directorNm;
    private String repNationCd;

    private String code;

}
