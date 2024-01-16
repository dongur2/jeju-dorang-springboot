package com.donguri.jejudorang.domain.community.service;


public interface BookmarkService {
    void changeCommunityLikedState(Long nowUserId, Long nowBoardId);
}
