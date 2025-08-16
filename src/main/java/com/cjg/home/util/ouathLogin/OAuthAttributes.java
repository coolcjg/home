package com.cjg.home.util.ouathLogin;

import com.cjg.home.code.ResultCode;
import com.cjg.home.code.SocialType;
import com.cjg.home.code.UserRole;
import com.cjg.home.domain.User;
import com.cjg.home.exception.CustomException;
import lombok.Builder;
import lombok.Getter;


import java.util.Map;
import java.util.UUID;

@Getter
public class OAuthAttributes {

    private String nameAttributeKey; // OAuth2 로그인 진행 시 키가 되는 필드 값, PK와 같은 의미
    private OAuth2UserInfo oauth2UserInfo; // 소셜 타입별 로그인 유저 정보(닉네임, 이메일, 프로필 사진 등등)

    @Builder
    public OAuthAttributes(String nameAttributeKey, OAuth2UserInfo oAuth2UserInfo){
        this.nameAttributeKey = nameAttributeKey;
        this.oauth2UserInfo = oauth2UserInfo;
    }

    /*
    SocialType에 맞는 메소드를 호출하여 OAuthAttributes 객체 반환
    userNameAttributeName : OAuth2 로그인시 키가 되는 값
    attributes : OAuth 서비스의 유저 정보들
     */

    public static OAuthAttributes of(SocialType socialType, String userNameAttributeName, Map<String, Object> attributes ){
        switch(socialType){
            case NAVER:
                return ofNaver(userNameAttributeName, attributes);
            case KAKAO:
                return ofKakao(userNameAttributeName, attributes);
            default:
                throw new CustomException(ResultCode.OAUTH_LOGIN_INVALID_SOCIAL_TYPE);
        }
    }

    private static OAuthAttributes ofKakao(String userNameAttributeName, Map<String, Object> attributes ){
        return  OAuthAttributes.builder()
                .nameAttributeKey(userNameAttributeName)
                .oAuth2UserInfo(new KakaoOAuth2UserInfo(attributes))
                .build();
    }

    private static OAuthAttributes ofNaver(String userNameAttributeName, Map<String, Object> attributes){
        return OAuthAttributes.builder()
                .nameAttributeKey(userNameAttributeName)
                .oAuth2UserInfo(new NaverOAuth2UserInfo(attributes))
                .build();
    }

    public User toEntity(SocialType socialType, OAuth2UserInfo oAuth2UserInfo){

        String uuid = UUID.randomUUID().toString();
        return User.builder()
                .userId(uuid)
                .socialType(socialType)
                .email(uuid + "@socialUser.com")
                .name(oauth2UserInfo.getNickname())
                .image(oauth2UserInfo.getImageUrl())
                .auth(UserRole.ADMIN.getValue())
                .build();
    }









}
