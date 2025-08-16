package com.cjg.home.util.ouathLogin;

import com.cjg.home.code.ResultCode;
import com.cjg.home.config.jwt.JwtTokenProvider;
import com.cjg.home.exception.CustomException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Value("${cookie.domain}")
    private String cookieDomain;

    private final JwtTokenProvider jwtService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication){
        log.info("OAuth2 Login Success");
        try{
            CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();
            loginSuccess(request, response, oAuth2User);
        }catch(IOException e){
            throw new CustomException(ResultCode.OAUTH_LOGIN_EXCEPTION);
        }
    }

    private void loginSuccess(HttpServletRequest request, HttpServletResponse response, CustomOAuth2User oAuth2User) throws IOException {
        log.info("getEmail{}", oAuth2User.getEmail());
        String accessToken = jwtService.createAccessToken(oAuth2User.getEmail());
        String refreshToken = jwtService.createRefreshToken(oAuth2User.getEmail());

        ResponseCookie responseCookie = ResponseCookie.from("accessToken", accessToken)
                .httpOnly(true)
                .secure(false)
                .path("/")
                //.maxAge(60*30) 세션으로 설정
                .domain(cookieDomain)
                .build();

        ResponseCookie responseCookie2 = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(false)
                .path("/")
                //.maxAge(60*60*10) 세션으로 설정
                .domain(cookieDomain)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, responseCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, responseCookie2.toString());

        getRedirectStrategy().sendRedirect(request, response, "/post/list");
    }




}
