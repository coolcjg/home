package com.cjg.home.config.jwt;

import org.springframework.stereotype.Component;

@Component
public class JwtTokenParser {
    public String parse(String withBearerToken) {
        return withBearerToken.split(" ")[1];
    }
}