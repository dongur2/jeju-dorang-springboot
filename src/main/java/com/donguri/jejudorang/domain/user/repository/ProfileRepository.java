package com.donguri.jejudorang.domain.user.repository;

import com.donguri.jejudorang.domain.user.entity.Profile;
import com.donguri.jejudorang.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProfileRepository extends JpaRepository<Profile, Long> {

    Optional<Profile> findByUser(User user);
}
