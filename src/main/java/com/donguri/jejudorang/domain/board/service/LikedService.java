package com.donguri.jejudorang.domain.board.service;


public interface LikedService {
    void updateBoardLikedState(Long nowUserId, Long nowBoardId);
}
