package com.cjg.home.controller;

import com.cjg.home.dto.request.ApiUrlDto;
import com.cjg.home.dto.response.UserLoginResponseDto;
import com.cjg.home.service.OAuth2LoginService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.io.UnsupportedEncodingException;

@Log4j2
@RequiredArgsConstructor
@Controller
public class OAuth2LoginController {

    private final OAuth2LoginService  oAuth2LoginService;

    @Value("${cookie.domain}")
    private String cookieDomain;

    @PostMapping("/getNaverApiUrl")
    @ResponseBody
    public String getNaverApiUrl(HttpSession session, @RequestBody @Valid ApiUrlDto dto) throws UnsupportedEncodingException {
        return oAuth2LoginService.getNaverApiUrl(session, dto.getSocialType());
    }


    @GetMapping("/login/oauth2/code/naver")
    public RedirectView naverLoginProcess(HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {
        UserLoginResponseDto userLoginResponseDto = oAuth2LoginService.naverLoginProcess(request);

        ResponseCookie responseCookie = ResponseCookie.from("accessToken",userLoginResponseDto.getAccessToken())
                .httpOnly(true)
                .secure(false)
                .path("/")
                //.maxAge(60*30) 세션으로 설정
                .domain(cookieDomain)
                .build();

        ResponseCookie responseCookie2 = ResponseCookie.from("refreshToken",userLoginResponseDto.getRefreshToken())
                .httpOnly(true)
                .secure(false)
                .path("/")
                //.maxAge(60*60*10) 세션으로 설정
                .domain(cookieDomain)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, responseCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, responseCookie2.toString());

        return new RedirectView("/post/list");

    }

    @GetMapping("/login/oauth2/code/kakao")
    public RedirectView kakaoLoginProcess(HttpServletRequest request, HttpServletResponse response, @RequestParam String code) throws UnsupportedEncodingException {
        UserLoginResponseDto userLoginResponseDto = oAuth2LoginService.kakaoLoginProcess(request, code);

        ResponseCookie responseCookie = ResponseCookie.from("accessToken",userLoginResponseDto.getAccessToken())
                .httpOnly(true)
                .secure(false)
                .path("/")
                //.maxAge(60*30) 세션으로 설정
                .domain(cookieDomain)
                .build();

        ResponseCookie responseCookie2 = ResponseCookie.from("refreshToken",userLoginResponseDto.getRefreshToken())
                .httpOnly(true)
                .secure(false)
                .path("/")
                //.maxAge(60*60*10) 세션으로 설정
                .domain(cookieDomain)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, responseCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, responseCookie2.toString());

        return new RedirectView("/post/list");

    }
}
