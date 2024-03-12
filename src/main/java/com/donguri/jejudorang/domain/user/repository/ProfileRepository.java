package com.donguri.jejudorang.domain.user.repository;

import com.donguri.jejudorang.domain.user.entity.Profile;
import com.donguri.jejudorang.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ProfileRepository extends JpaRepository<Profile, Long> {

    Optional<Profile> findByUser(User user);

    // 관리자 - 현재 사용되고있는 프로필 사진 이름 모두 조회
    @Query(value = "select p.imgName from Profile p")
    List<String> findAllImgName();
}