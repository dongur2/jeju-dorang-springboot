package com.donguri.jejudorang.domain.community.service.bookmark;


public interface BookmarkService {
    void changeCommunityLikedState(Long nowUserId, Long nowBoardId);
}
