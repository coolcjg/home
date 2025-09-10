package com.cjg.home.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Builder
@Getter
@ToString
public class MovieListRequestDto {

    String movieNm;
    String directorNm;
    String openStartDt;
    String openEndDt;
    String repNationCd;
    String movieTypeCd;

    Integer curPage;
    Integer itemPerPage;

    public MultiValueMap<String, String> toParamMap() {
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        if (movieNm != null) map.add("movieNm", movieNm);
        if (directorNm != null) map.add("directorNm", directorNm);
        if (openStartDt != null) map.add("openStartDt", openStartDt);
        if (openEndDt != null) map.add("openEndDt", openEndDt);
        if (repNationCd != null) map.add("repNationCd", repNationCd);
        if (movieTypeCd != null) map.add("movieTypeCd", movieTypeCd);
        map.add("curPage", curPage+"");
        map.add("itemPerPage", itemPerPage+"");

        return map;
    }
}
