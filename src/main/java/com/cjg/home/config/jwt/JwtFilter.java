package com.cjg.home.config.jwt;


import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Log4j2
public class JwtFilter extends OncePerRequestFilter {

	private final JwtTokenProvider jwtTokenProvider;

	@Value("${cookie.domain}")
	private String cookieDomain;

	@Override
	protected void doFilterInternal(
			HttpServletRequest request,
			HttpServletResponse response,
			FilterChain filterChain
	) throws ServletException, IOException {
		String[] token = jwtTokenProvider.resolveToken(request);
		String prevAccessToken = token[0];

		if(token[0] != null && token[1] != null){

			try {
				if (jwtTokenProvider.validateToken(token)) {

					if(!prevAccessToken.equals(token[0])){

                        //accessToken 만료시간으로 인해, 새로 발급 받은 accessToken을 쿠키에 저장한다.
						Cookie cookie = new Cookie("accessToken", token[0]);
						cookie.setHttpOnly(true);
						cookie.setSecure(false);
						cookie.setPath("/");
						//cookie.setMaxAge(60*30);
						cookie.setDomain(cookieDomain);

						response.addCookie(cookie);
					}

					//토큰이 있다는것은 로그인을 했다는것이기 때문에 추가로 인증로직을 수행하지 않는다.
					Authentication auth = jwtTokenProvider.getAuthentication(token[0]);
					SecurityContextHolder.getContext().setAuthentication(auth);
				}else{
					//토큰 처리가 실패했을 경우에 로그인 페이지 이동
					invalidToken(request, response);
				}
			} catch (RedisConnectionFailureException | ExpiredJwtException e) {
				SecurityContextHolder.clearContext();
				log.error(e);

				//토큰 처리가 실패했을 경우에 로그인 페이지 이동
				invalidToken(request, response);
			}
        }

		filterChain.doFilter(request, response);
	}

	private void invalidToken(HttpServletRequest request, HttpServletResponse response) throws IOException{
		jwtTokenProvider.removeTokenFromCookie(request, response);
		response.sendRedirect("/user/login");
	}
}
