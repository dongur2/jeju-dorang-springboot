package com.donguri.jejudorang.domain.board.repository;

import com.donguri.jejudorang.domain.board.entity.Board;
import com.donguri.jejudorang.domain.board.entity.BoardType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BoardRepository extends JpaRepository<Board, Long> {
    Page<Board> findAllByType(BoardType boardType, Pageable pageable);
}
