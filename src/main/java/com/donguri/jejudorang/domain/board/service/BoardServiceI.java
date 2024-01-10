package com.donguri.jejudorang.domain.board.service;

import com.donguri.jejudorang.domain.board.dto.request.BoardUpdateRequestDto;
import com.donguri.jejudorang.domain.board.dto.request.BoardWriteRequestDto;
import com.donguri.jejudorang.domain.board.entity.Board;
import com.donguri.jejudorang.domain.board.repository.BoardRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class BoardServiceI implements BoardService{
    @Autowired
    private BoardRepository boardRepository;

    @Override
    public List<Board> getAllPosts() {
        return boardRepository.findAll();
    }

    @Override
    public Board getPost(Long id) {
        return boardRepository.findById(id).get();
    }

    @Override
    public Board savePost(BoardWriteRequestDto post) {
        return boardRepository.save(post.toEntity());
    }

    @Override
    @Transactional
    public void updatePost(Long id, BoardUpdateRequestDto post) {
        Board update = Board.builder()
                .id(id)
                .title(post.getTitle())
                .tags(post.getTags())
                .content(post.getContent())
                .type(post.getType())
                .build();
        boardRepository.save(update);
    }
}
