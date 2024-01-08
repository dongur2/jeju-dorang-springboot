package com.donguri.jejudorang.domain.board.service;

import com.donguri.jejudorang.domain.board.dto.BoardWriteRequestDto;
import com.donguri.jejudorang.domain.board.entity.Board;
import com.donguri.jejudorang.domain.board.repository.BoardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BoardServiceI implements BoardService{
    @Autowired
    private BoardRepository boardRepository;

    @Override
    public Board savePost(BoardWriteRequestDto post) {
        return boardRepository.save(post.toEntity());
    }
}
