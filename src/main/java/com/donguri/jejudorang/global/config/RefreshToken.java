package com.donguri.jejudorang.global.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@Getter
@NoArgsConstructor
@RedisHash(value = "refreshToken", timeToLive = 14440)
public class RefreshToken {
    @Id
    private String refreshToken;

    private Long userId;

    private Collection<? extends GrantedAuthority> authorities;

    @Builder
    public RefreshToken(String refreshToken, Long userId, Collection<? extends GrantedAuthority> authorities) {
        this.refreshToken = refreshToken;
        this.userId = userId;
        this.authorities = authorities;
    }
}
