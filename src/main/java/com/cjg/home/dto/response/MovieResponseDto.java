package com.cjg.home.dto.response;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@ToString
public class MovieResponseDto {
    private String movieCd;
    private String movieNm;
    private String prdtYear;
    private String openDt;
    private String repNationNm;
    private String repGenreNm;
    private String repDirector;
}
