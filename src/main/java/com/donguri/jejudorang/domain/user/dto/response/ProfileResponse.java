package com.donguri.jejudorang.domain.user.dto.response;

import com.donguri.jejudorang.domain.user.entity.User;
import lombok.Builder;

@Builder
public record ProfileResponse (
        String loginType,
        String nickname,
        String externalId,
        String email,
        String img
){

    public ProfileResponse from(User user) {
        return ProfileResponse.builder()
                .loginType(user.getLoginType().name())
                .nickname(user.getProfile().getNickname())
                .externalId(user.getProfile().getExternalId())
                .email(user.getAuth().getEmail())
                .img(user.getProfile().getImgUrl())
                .build();
    }

}