package com.donguri.jejudorang.domain.user.repository.auth;

import com.donguri.jejudorang.domain.user.entity.auth.Password;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PasswordRepository extends JpaRepository<Password, Long> {
}
