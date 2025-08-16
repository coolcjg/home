package com.cjg.home.util.ouathLogin;


import com.cjg.home.code.ResultCode;
import com.cjg.home.response.Response;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;


@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2LoginFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {

        // HTTP 응답 상태 코드를 401로 설정
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        // 응답의 Content-Type을 JSON 형식으로 설정
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");

        ResponseEntity<Response<Void>> result = ResponseEntity.status(Integer.parseInt(ResultCode.OAUTH_LOGIN_EXCEPTION.getCode()))
                .body(Response.fail(ResultCode.OAUTH_LOGIN_EXCEPTION));

        // ObjectMapper를 사용하여 Response 객체를 JSON으로 직렬화하고, 클라이언트에 응답으로 보냄
        objectMapper.writeValue(response.getWriter(), result);

        // 로그인 실패 로그 기록
        log.info("소셜 로그인에 실패했습니다. 에러 메시지 : {}", exception.getMessage());
    }
}
