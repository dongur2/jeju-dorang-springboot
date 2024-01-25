package com.donguri.jejudorang.domain.user.repository.auth;

import com.donguri.jejudorang.domain.user.entity.auth.SocialLogin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SocialLoginRepository extends JpaRepository<SocialLogin, Long> {
}
