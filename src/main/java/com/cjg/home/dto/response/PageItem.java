package com.cjg.home.dto.response;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@ToString
@Getter
@Setter
public class PageItem {

    private int pageNumber;
    private String pageUrl;
}
