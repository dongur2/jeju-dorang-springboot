package com.donguri.jejudorang.domain.user.repository;

import com.donguri.jejudorang.domain.user.entity.Authentication;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthenticationRepository extends JpaRepository<Authentication, Long> {
}
