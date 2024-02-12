package com.donguri.jejudorang.global.auth.jwt;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;


@Getter
@NoArgsConstructor
@RedisHash(value = "refreshToken", timeToLive = 3600) // 1시간
public class RefreshToken {
    @Id
    private String refreshToken;

    @Indexed
    private String userId;

    @Builder
    public RefreshToken(String refreshToken, String userId) {
        this.refreshToken = refreshToken;
        this.userId = userId;
    }
}
