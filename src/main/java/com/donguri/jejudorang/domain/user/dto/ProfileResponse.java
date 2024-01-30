package com.donguri.jejudorang.domain.user.dto;

import com.donguri.jejudorang.domain.user.entity.User;
import lombok.Builder;

@Builder
public record ProfileResponse (
        String nickname,
        String externalId,
        String email,
        String img
){

    public ProfileResponse from(User user) {
        return ProfileResponse.builder()
                .nickname(user.getProfile().getNickname())
                .externalId(user.getProfile().getExternalId())
                .email(user.getAuth().getEmail())
                .img(user.getProfile().getImgUrl())
                .build();
    }

}