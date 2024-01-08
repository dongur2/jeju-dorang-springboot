package com.donguri.jejudorang.domain.board.repository;

import com.donguri.jejudorang.domain.board.entity.Board;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardRepository extends JpaRepository<Board, Long> {
}
