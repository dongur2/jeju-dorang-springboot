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
    @Transactional
    public Board getPost(Long id) {
        Board found = boardRepository.findById(id).get();
        found.upViewCount();
        return found;
    }

    @Override
    @Transactional
    public Board savePost(BoardWriteRequestDto post) {
        Board newPost = Board.builder()
                .title(post.getTitle())
                .content(post.getContent())
                .build();
        newPost.setBoardType(post.getType());
        newPost.setDefaultJoinState();
        return boardRepository.save(newPost);
    }

    @Override
    @Transactional
    public void updatePost(Long id, BoardUpdateRequestDto post) {
        Board update = Board.builder()
                .id(id)
                .title(post.getTitle())
                .tags(post.getTags())
                .content(post.getContent())
                .build();
        update.setBoardType(post.getType());
        update.setDefaultJoinState();
        boardRepository.save(update);
    }

    @Override
    @Transactional
    public void changePartyJoinState(Long id) {
        Board found = boardRepository.findById(id).get();
        found.changeJoinState();
    }
}
