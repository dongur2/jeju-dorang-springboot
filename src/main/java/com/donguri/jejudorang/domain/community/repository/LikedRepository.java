package com.donguri.jejudorang.domain.community.repository;

import com.donguri.jejudorang.domain.community.entity.Liked;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikedRepository extends JpaRepository<Liked, Long> {
//    Optional<Liked> findByUserIdAndBoardId(Long UserId, Long BoardId);
}
