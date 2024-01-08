package com.donguri.jejudorang.domain.board.service;

import com.donguri.jejudorang.domain.board.dto.BoardWriteRequestDto;
import com.donguri.jejudorang.domain.board.entity.Board;

public interface BoardService {
    Board savePost(BoardWriteRequestDto post);
}
