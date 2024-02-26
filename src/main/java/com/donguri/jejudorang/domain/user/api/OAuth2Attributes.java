package com.donguri.jejudorang.domain.user.api;

import com.donguri.jejudorang.domain.user.entity.*;
import com.donguri.jejudorang.domain.user.entity.auth.SocialLogin;
import com.donguri.jejudorang.domain.user.repository.RoleRepository;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Slf4j
@Getter
public class OAuth2Attributes {
    private String attributeKey;
    private OAuth2UserInfo oAuth2UserInfo;

    @Value("${aws.s3.default-img.name}")
    private String defaultImgName;
    @Value("${aws.s3.default-img.url}")
    private String defaultImgUrl;

    @Builder
    public OAuth2Attributes(String attributeKey, OAuth2UserInfo oAuth2UserInfo) {
        this.attributeKey = attributeKey;
        this.oAuth2UserInfo = oAuth2UserInfo;
    }

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
                .imgName(defaultImgName)
                .imgUrl(defaultImgUrl)
                .build();

        SocialLogin socialLogin = SocialLogin.builder()
                .user(user)
                .socialCode(oAuth2UserInfo.getId())
                .socialExternalId(oAuth2UserInfo.getEmail())
                .build();

        user.updateProfile(profile);
        user.updateSocialLogin(socialLogin);

        return user;
    }

}
