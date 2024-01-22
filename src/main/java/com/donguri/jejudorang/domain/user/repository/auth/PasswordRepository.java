package com.donguri.jejudorang.domain.user.repository.auth;

import com.donguri.jejudorang.domain.user.entity.User;
import com.donguri.jejudorang.domain.user.entity.auth.Password;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PasswordRepository extends JpaRepository<Password, Long> {
    Optional<Password> findByUser(User user);
}
