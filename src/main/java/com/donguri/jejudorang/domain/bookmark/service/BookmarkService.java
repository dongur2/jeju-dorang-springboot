package com.donguri.jejudorang.domain.bookmark.service;


import com.donguri.jejudorang.domain.user.entity.User;

public interface BookmarkService {
    void changeCommunityLikedState(User nowUser, Long nowBoardId);
}
