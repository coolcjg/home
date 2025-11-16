package com.cjg.home.config.jwt;

import com.cjg.home.code.ResultCode;
import com.cjg.home.exception.CustomAuthException;
import com.cjg.home.exception.CustomException;
import com.cjg.home.service.RedisService;
import com.cjg.home.service.UserDetailsServiceImpl;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@Log4j2
public class JwtTokenProvider {

	private final RedisTemplate<String, String> redisTemplate;

	@Value("${spring.jwt.secret}")
	private String secretKey;

	@Value("${cookie.domain}")
	private String cookieDomain;

	@Value("${spring.jwt.token.access-expiration-time}")
	public long accessExpirationTime;

	@Value("${spring.jwt.token.refresh-expiration-time}")
	public long refreshExpirationTime;

	private final UserDetailsServiceImpl userDetailsService;

    private final RedisService redisService;

    private final JwtTokenParser jwtTokenParser;

	/**
	 * Access 토큰 생성
	 */
	public String createAccessToken(Authentication authentication){
        Claims claims = Jwts.claims().subject(authentication.getName()).build();
		Date now = new Date();
		Date expireDate = new Date(now.getTime() + accessExpirationTime*60*1000);

		return Jwts.builder()
				.claims(claims)
				.issuedAt(now)
				.expiration(expireDate)
				.signWith(Keys.hmacShaKeyFor(secretKey.getBytes()))
				.compact();
	}

	public String createAccessToken(String userId){
        Claims claims = Jwts.claims().subject(userId).build();
		Date now = new Date();
		Date expireDate = new Date(now.getTime() + accessExpirationTime*60*1000);

		return Jwts.builder()
				.claims(claims)
				.issuedAt(now)
				.expiration(expireDate)
				.signWith(Keys.hmacShaKeyFor(secretKey.getBytes()))
				.compact();
	}

	/**
	 * Refresh 토큰 생성
	 */
	public String createRefreshToken(Authentication authentication){
        Claims claims = Jwts.claims().subject(authentication.getName()).build();
		Date now = new Date();
		Date expireDate = new Date(now.getTime() + refreshExpirationTime*60*1000);

		String refreshToken = Jwts.builder()
				.claims(claims)
				.issuedAt(now)
				.expiration(expireDate)
				.signWith(Keys.hmacShaKeyFor(secretKey.getBytes()))
				.compact();

        redisService.save(authentication.getName(), refreshToken, refreshExpirationTime*60*1000, TimeUnit.MILLISECONDS);
		return refreshToken;
	}

    public String createRefreshToken(String userId){
        Claims claims = Jwts.claims().subject(userId).build();
        Date now = new Date();
        Date expireDate = new Date(now.getTime() + refreshExpirationTime*60*1000);

        String refreshToken = Jwts.builder()
                .claims(claims)
                .issuedAt(now)
                .expiration(expireDate)
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes()))
                .compact();

        redisService.save(userId, refreshToken, refreshExpirationTime*60*1000, TimeUnit.MILLISECONDS);
        return refreshToken;
    }

	/**
	 * 토큰으로부터 클레임을 만들고, 이를 통해 User 객체 생성해 Authentication 객체 반환
	 */
	public Authentication getAuthentication(String token){

		String userPrincipal = getUserPrincipal(token);
		UserDetails userDetails = userDetailsService.loadUserByUsername(userPrincipal);

		return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
	}

	public String getUserPrincipal(String token) throws ExpiredJwtException{
		return Jwts.parser()
				.verifyWith(Keys.hmacShaKeyFor(secretKey.getBytes()))
				.build()
				.parseSignedClaims(token)
				.getPayload().getSubject();
	}

	/**
	 * http 헤더로부터 bearer 토큰을 가져옴.
	 */
	public String resolveToken(HttpServletRequest req) {
        String authorization = req.getHeader("Authorization");

        if(authorization != null){
            return jwtTokenParser.parse(authorization);
        }

		return null;
	}

	/**
	 * Access 토큰을 검증
	 */
	public boolean validateToken(String token) throws CustomAuthException {
		try{
			Jwts.parser().verifyWith(Keys.hmacShaKeyFor(secretKey.getBytes())).build().parseSignedClaims(token);
			return true;
		} catch(ExpiredJwtException e) {
            log.info("ExpiredJwtException : ", e);
		} catch(JwtException e) {
            log.error("JwtException : ", e);
		}

        return false;
	}

	public boolean validateRefreshToken(String refreshToken){
		try{
			Jwts.parser().verifyWith(Keys.hmacShaKeyFor(secretKey.getBytes())).build().parseSignedClaims(refreshToken);
			return true;
		} catch(ExpiredJwtException e) {
			throw new CustomException(ResultCode.JWT_EXPIRE);
		}
	}
}