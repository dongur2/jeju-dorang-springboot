package com.donguri.jejudorang.domain.user.repository;

import com.donguri.jejudorang.domain.user.entity.LoginType;
import com.donguri.jejudorang.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    @Override
    Optional<User> findById(Long id);

    @Query("select u from User u where u.profile.externalId=:eid")
    Optional<User> findByExternalId(@Param("eid") String externalId);

    @Query("select u from User u where u.auth.email=:email")
    Optional<User> findByEmail(@Param("email") String email);

    @Query("select u from User u where u.auth.email=:email and u.profile.externalId=:eid")
    Optional<User> findByEmailAndExternalId(@Param("email") String email, @Param("eid") String externalId);

    @Query("select u from User u where u.loginType=:type and u.socialLogin.socialCode=:code")
    Optional<User> findByLoginTypeAndSocialCode(@Param("type") LoginType type, @Param("code") String socialCode);

    @Query("select u from User u where u.loginType=:type and u.id=:id")
    Optional<User> findByLoginTypeAndId(@Param("type") LoginType type, @Param("id") String id);
}
