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
public class AlarmListResponseDto {
    private List<AlarmResponseDto> list;
    private int totalPage;
    private Long totalCount;

    private int pageNumber;
}
