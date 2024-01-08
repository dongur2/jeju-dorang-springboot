package com.donguri.jejudorang.domain.board.service;

import com.donguri.jejudorang.domain.board.dto.request.BoardWriteRequestDto;
import com.donguri.jejudorang.domain.board.entity.Board;

import java.util.List;

public interface BoardService {
    List<Board> getAllPosts();
    Board savePost(BoardWriteRequestDto post);
}
