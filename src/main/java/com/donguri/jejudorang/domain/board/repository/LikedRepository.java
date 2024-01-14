package com.donguri.jejudorang.domain.board.repository;

import com.donguri.jejudorang.domain.board.entity.Liked;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikedRepository extends JpaRepository<Liked, Long> {
}
