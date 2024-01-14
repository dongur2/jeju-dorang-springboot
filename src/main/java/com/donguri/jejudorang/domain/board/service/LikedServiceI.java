package com.donguri.jejudorang.domain.board.service;

import com.donguri.jejudorang.domain.board.repository.BoardRepository;
import com.donguri.jejudorang.domain.board.repository.LikedRepository;
import com.donguri.jejudorang.domain.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LikedServiceI implements LikedService{
    @Autowired
    private LikedRepository likedRepository;
    @Autowired
    private BoardRepository boardRepository;
    @Autowired
    private UserRepository userRepository;

    @Override
    public void updateBoardLikedState(Long nowUserId, Long nowBoardId) {

    }
}
