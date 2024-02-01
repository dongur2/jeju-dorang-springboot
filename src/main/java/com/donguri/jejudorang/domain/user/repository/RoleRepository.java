package com.donguri.jejudorang.domain.user.repository;

import com.donguri.jejudorang.domain.user.entity.ERole;
import com.donguri.jejudorang.domain.user.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(ERole name);
}
