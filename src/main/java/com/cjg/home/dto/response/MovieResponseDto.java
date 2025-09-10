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
public class MovieResponseDto {
    private String movieCd;
    private String movieNm;
    private int showTm;
    private int prdtYear;
    private String openDt;
    private String prdtStatNm;
    private String typeNm;
    private String repNationNm;
    private String repGenreNm;
    private String repDirector;
    private String code;

    List<String> nations;
    List<String> genres;
    List<String> directors;
    List<String> actors;
}
