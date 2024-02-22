package com.donguri.jejudorang.domain.user.dto.response;


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

}
