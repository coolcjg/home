package com.cjg.home.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.security.SecureRandom;

@RestController
public class OAuth2LoginController {

    @Value("${naver.client-id}")
    private String clientId;

    @Value("${naver.client-secret}")
    private String clientSecret;

    @Value("${naver.callback-url}")
    private String callbackUrl;

    @PostMapping("/getNaverApiUrl")
    public String getNaverApiUrl(HttpSession session) throws UnsupportedEncodingException {

        String redirectURI = URLEncoder.encode(callbackUrl, "UTF-8");
        SecureRandom random = new SecureRandom();
        String state = new BigInteger(130, random).toString(32);

        String apiURL = "https://nid.naver.com/oauth2.0/authorize?response_type=code"
                + "&client_id=" + clientId
                + "&redirect_uri=" + redirectURI
                + "&state=" + state;

        session.setAttribute("state", state);

        return apiURL;
    }
}
