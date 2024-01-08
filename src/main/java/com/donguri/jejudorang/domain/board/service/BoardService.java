package com.donguri.jejudorang.domain.board.service;

import com.donguri.jejudorang.domain.board.dto.request.BoardWriteRequestDto;
import com.donguri.jejudorang.domain.board.entity.Board;

public interface BoardService {
    Board savePost(BoardWriteRequestDto post);
}
