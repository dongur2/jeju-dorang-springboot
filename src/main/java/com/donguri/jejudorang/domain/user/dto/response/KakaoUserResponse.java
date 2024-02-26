package com.donguri.jejudorang.domain.user.dto.response;


import com.donguri.jejudorang.domain.user.entity.*;
import com.donguri.jejudorang.domain.user.entity.auth.SocialLogin;
import lombok.Builder;


@Builder
public record KakaoUserResponse(
    String id,
    String email,
    String nickname
) {

    public User toEntity() {
        User user = User.builder()
                .loginType(LoginType.KAKAO)
                .build();

        SocialLogin sns = SocialLogin.builder()
                .socialCode(id)
                .socialExternalId(email)
                .user(user)
                .build();

        Profile profile = Profile.builder()
                .user(user)
                .externalId(email)
                .nickname(nickname)
                .build();

        user.updateSocialLogin(sns);
        user.updateProfile(profile);

        return user;
    }

}
