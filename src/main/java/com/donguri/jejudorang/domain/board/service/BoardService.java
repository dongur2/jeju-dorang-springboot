package com.donguri.jejudorang.domain.board.service;

import com.donguri.jejudorang.domain.board.dto.request.BoardUpdateRequestDto;
import com.donguri.jejudorang.domain.board.dto.request.BoardWriteRequestDto;
import com.donguri.jejudorang.domain.board.dto.response.BoardDetailResponseDto;
import com.donguri.jejudorang.domain.board.dto.response.BoardListResponseDto;
import com.donguri.jejudorang.domain.board.entity.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BoardService {
    Page<BoardListResponseDto> getAllPosts(Pageable pageable);

    BoardDetailResponseDto getPost(Long id);

    Board savePost(BoardWriteRequestDto post);

    void updatePost(Long id, BoardUpdateRequestDto post);

    void changePartyJoinState(Long id);

}
