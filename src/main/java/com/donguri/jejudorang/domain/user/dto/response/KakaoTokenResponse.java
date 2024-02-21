package com.donguri.jejudorang.domain.user.dto.response;

public record KakaoTokenResponse(
        String token_type, // 토큰 타입, bearer로 고정
        String access_token,
        String expires_in,
        String refresh_token,
        String refresh_token_expires_in
) {


}
