package com.donguri.jejudorang.domain.user.repository;

import com.donguri.jejudorang.domain.user.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Long, Role> {
}
