package com.cjg.home.util.ouathLogin;

import com.cjg.home.code.ResultCode;
import com.cjg.home.code.SocialType;
import com.cjg.home.code.UserRole;
import com.cjg.home.domain.User;
import com.cjg.home.exception.CustomException;
import com.cjg.home.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.task.TaskExecutionProperties;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;

@Log4j2
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserRepository userRepository;





    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {


        log.info("CustomOAuth2UserService loadUser 실행 - OAuth2 로그인 요청 진입");

        /*
        DefaultOAuth2UserService 객체를 생성하여, DefaultOAuth2User 객체를 생성 후 반환
         */

        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        /*
        userRequest에서 registrationId 추출 후 SocialType 저장
         */
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        SocialType socialType = getSocialType(registrationId);

        String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();
        Map<String, Object> attributes = oAuth2User.getAttributes();

        OAuthAttributes extractAttributes = OAuthAttributes.of(socialType, userNameAttributeName, attributes);

        User createdUser = getUser(extractAttributes, socialType);

        //DefaultOAuth2User를 구현한 CustomOAuth2User객체를 생성해서 반환
        return new CustomOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority(createdUser.getAuth())),
                attributes,
                extractAttributes.getNameAttributeKey(),
                createdUser.getEmail(),
                UserRole.valueOf(createdUser.getAuth())
        );
    }

    private SocialType getSocialType(String registrationId) {
        return switch (registrationId) {
            case "NAVER" -> SocialType.NAVER;
            case "KAKAO" -> SocialType.KAKAO;
            default -> throw new CustomException(ResultCode.OAUTH_LOGIN_INVALID_SOCIAL_TYPE);
        };
    }

    /*
    소셜 로그인의 식별값 id를 통해 회원을 찾아 반환하는 메소드
     */
    private User getUser(OAuthAttributes attributes, SocialType socialType) {
        User user = userRepository.findBySocialTypeAndSocialId(socialType, attributes.getOauth2UserInfo().getId());

        if(user == null){
            user = userRepository.save(attributes.toEntity(socialType, attributes.getOauth2UserInfo()));
        }
        return user;
    }



}
