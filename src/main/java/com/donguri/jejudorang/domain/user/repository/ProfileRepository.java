package com.donguri.jejudorang.domain.user.repository;

import com.donguri.jejudorang.domain.user.entity.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfileRepository extends JpaRepository<Profile, Long> {
}
