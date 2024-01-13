package com.donguri.jejudorang.domain.board.service;

import com.donguri.jejudorang.domain.board.dto.request.BoardUpdateRequestDto;
import com.donguri.jejudorang.domain.board.dto.request.BoardWriteRequestDto;
import com.donguri.jejudorang.domain.board.dto.response.BoardDetailResponseDto;
import com.donguri.jejudorang.domain.board.entity.Board;

import java.util.List;

public interface BoardService {
    List<Board> getAllPosts();

    BoardDetailResponseDto getPost(Long id);

    Board savePost(BoardWriteRequestDto post);

    void updatePost(Long id, BoardUpdateRequestDto post);

    void changePartyJoinState(Long id);
}
