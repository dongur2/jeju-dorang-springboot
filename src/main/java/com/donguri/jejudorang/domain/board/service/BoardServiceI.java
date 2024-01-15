package com.donguri.jejudorang.domain.board.service;

import com.donguri.jejudorang.domain.board.dto.request.BoardUpdateRequestDto;
import com.donguri.jejudorang.domain.board.dto.request.BoardWriteRequestDto;
import com.donguri.jejudorang.domain.board.dto.response.BoardDetailResponseDto;
import com.donguri.jejudorang.domain.board.entity.Board;
import com.donguri.jejudorang.domain.board.repository.BoardRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
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
    public BoardDetailResponseDto getPost(Long id) {
        Board found = boardRepository.findById(id).get();
        found.upViewCount();

        return BoardDetailResponseDto.builder()
                .id(found.getId())
                .type(found.getType())
                .joining(found.getJoining())
                .title(found.getTitle())
                .createdAt(found.getCreatedAt())
                .updatedAt(found.getUpdatedAt())
                .viewCount(found.getViewCount())
                .content(found.getContent())
                .tags(found.getTags())
                .likedCount(found.getLiked().size())
                .build();
    }

    @Override
    @Transactional
    public Board savePost(BoardWriteRequestDto post) {
        List<String> splitTagStringToWrite = Arrays.stream(post.getTags().split(","))
                .toList();

        Board newPost = Board.builder()
                .title(post.getTitle())
                .tags(splitTagStringToWrite)
                .content(post.getContent())
                .build();
        newPost.setBoardType(post.getType());
        newPost.setDefaultJoinState();
        return boardRepository.save(newPost);
    }

    @Override
    @Transactional
    public void updatePost(Long id, BoardUpdateRequestDto post) {
        List<String> splitTagStringToUpdate = Arrays.stream(post.getTags().split(","))
                .toList();

        Board update = Board.builder()
                .id(id)
                .title(post.getTitle())
                .tags(splitTagStringToUpdate)
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
