package com.cjg.home.dto.response;

import lombok.Builder;

@Builder
public class JwtErrorResponse {
    int code;
    String message;
}