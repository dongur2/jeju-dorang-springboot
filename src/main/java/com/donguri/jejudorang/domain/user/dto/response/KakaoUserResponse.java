package com.donguri.jejudorang.domain.user.dto.response;


import com.donguri.jejudorang.domain.user.entity.*;
import com.donguri.jejudorang.domain.user.entity.auth.SocialLogin;

import java.util.HashSet;
import java.util.Set;

public record KakaoUserResponse(
    String id,
    KakaoAccount kakao_account
) {
    public record KakaoAccount(
            Profile profile,
            String email
    ) {
        public record Profile (
                String nickname
        ){}
    }

    public User to() {
        User user = User.builder()
                .loginType(LoginType.KAKAO)
                .build();

        SocialLogin sns = SocialLogin.builder()
                .socialCode(id)
                .socialExternalId(kakao_account.email)
                .user(user)
                .build();

        Profile profile = Profile.builder()
                .user(user)
                .externalId(kakao_account.email)
                .nickname(kakao_account.profile.nickname)
                .build();

        user.updateSocialLogin(sns);
        user.updateProfile(profile);

        return user;

    }

}
