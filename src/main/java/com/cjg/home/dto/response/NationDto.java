package com.cjg.home.dto.response;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@ToString
public class NationDto {
    private String fullCd;
    private String korNm;
}
