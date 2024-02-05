package com.donguri.jejudorang.global.config.jwt;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String> {
    Optional<RefreshToken> findByUserId(String userId);

    @Override
    Optional<RefreshToken> findById(String token);
}
