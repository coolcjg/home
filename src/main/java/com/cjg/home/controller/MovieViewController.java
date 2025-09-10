package com.cjg.home.controller;

import com.cjg.home.domain.CustomUserDetails;
import com.cjg.home.dto.request.MovieListRequestDto;
import com.cjg.home.service.MovieService;
import com.cjg.home.util.AuthCheck;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
@Log4j2
public class MovieViewController {

    private final MovieService movieService;
    private final AuthCheck authCheck;

    @GetMapping(value = "/movie/list")
    public String list(@AuthenticationPrincipal CustomUserDetails customUserDetails, Model model
            ,@RequestParam(required = false) String movieNm
            ,@RequestParam(required = false) String directorNm
            ,@RequestParam(required = false) String openStartDt
            ,@RequestParam(required = false) String openEndDt
            ,@RequestParam(required = false) String repNationCd
            ,@RequestParam(required = false) String movieTypeCd
            ,@RequestParam(required = false, defaultValue = "1") int curPage
            ,@RequestParam(required = false, defaultValue = "10") int itemPerPage) {

        MovieListRequestDto dto = MovieListRequestDto.builder()
                .movieNm(movieNm)
                .directorNm(directorNm)
                .openStartDt(openStartDt)
                .openEndDt(openEndDt)
                .repNationCd(repNationCd)
                .movieTypeCd(movieTypeCd)
                .curPage(curPage)
                .itemPerPage(itemPerPage)
                .build();

        model.addAttribute("data", movieService.list(dto));
        return "movie/list";
    }

//    @GetMapping(value = "/movie/{movieCd}")
//    public String view(@AuthenticationPrincipal CustomUserDetails customUserDetails, @PathVariable String movieCd, Model model){
//        model.addAttribute("data", movieService.view(customUserDetails, movieCd));
//        return "movie/view";
//    }

}
