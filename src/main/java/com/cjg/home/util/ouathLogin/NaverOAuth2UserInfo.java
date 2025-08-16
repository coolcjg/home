package com.cjg.home.util.ouathLogin;

import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
public class NaverOAuth2UserInfo extends OAuth2UserInfo{

    public NaverOAuth2UserInfo(Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    public String getId() {
        Map<String ,Object> response = (Map<String, Object>) attributes.get("response");
        return response != null ? (String)response.get("id"):null;
    }

    @Override
    public String getNickname() {
        Map<String ,Object> response = (Map<String, Object>) attributes.get("response");
        return response != null ? (String)response.get("name"):null;
    }

    @Override
    public String getImageUrl() {
        Map<String ,Object> response = (Map<String, Object>) attributes.get("response");
        return response != null ? (String)response.get("profile_image"):null;
    }
}
