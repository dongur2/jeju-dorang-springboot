package com.donguri.jejudorang.global.auth.oauth;

import com.donguri.jejudorang.domain.user.entity.*;
import com.donguri.jejudorang.domain.user.entity.auth.Authentication;
import com.donguri.jejudorang.domain.user.entity.auth.SocialLogin;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.UUID;

/*
* 소셜로그인별로 전달받는 데이터의 형식이 다르므로
* 데이터를 각각 처리하게 해주는 DTO
*
* */
@Slf4j
@Getter
public class OAuth2Attributes {
    private String attributeKey; // OAuth 2.0 프로바이더에서 사용자의 고유 식별자(attribute): 사용자 식별에 사용되는 속성
    private OAuth2UserInfo oAuth2UserInfo; // 소셜 로그인 조회 유저 정보

    @Builder
    public OAuth2Attributes(String attributeKey, OAuth2UserInfo oAuth2UserInfo) {
        this.attributeKey = attributeKey;
        this.oAuth2UserInfo = oAuth2UserInfo;
    }

    /*
    * 각 소셜 서비스에 맞는 메서드를 호출
    * */
    public static OAuth2Attributes of(LoginType loginType, String userAttributeName, Map<String, Object> attributes) {
        if(loginType == LoginType.KAKAO) {
            return ofKakao(userAttributeName, attributes);
        } else {
            return null;
        }
    }

    private static OAuth2Attributes ofKakao(String userAttributeName, Map<String, Object> attributes) {
        return OAuth2Attributes.builder()
                .attributeKey(userAttributeName)
                .oAuth2UserInfo(new KakaoOAuth2UserInfo(attributes))
                .build();
    }

    public User toEntity(LoginType loginType, OAuth2UserInfo oAuth2UserInfo) {
        User user = User.builder()
                .loginType(loginType)
                .build();

        Profile profile = Profile.builder()
                .user(user)
                .nickname(oAuth2UserInfo.getNickname())
                .externalId(oAuth2UserInfo.getEmail())
                .build();

        SocialLogin socialLogin = SocialLogin.builder()
                .user(user)
                .socialCode(oAuth2UserInfo.getId())
                .socialExternalId(oAuth2UserInfo.getEmail())
                .build();

        Authentication authentication = Authentication.builder()
                        .user(user)
                        .email(UUID.randomUUID() + "@socialUser.com") // JWT 토큰 발급시 password로 사용
                        .agreement(AgreeRange.NECESSARY)
                        .build();

        user.updateProfile(profile);
        user.updateSocialLogin(socialLogin);
        user.updateAuth(authentication);

        return user;
    }

}
