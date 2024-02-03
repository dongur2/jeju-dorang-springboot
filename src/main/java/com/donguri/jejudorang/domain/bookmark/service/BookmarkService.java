package com.donguri.jejudorang.domain.bookmark.service;


import com.donguri.jejudorang.domain.user.entity.User;

public interface BookmarkService {

    void makeBookmarkOnCommunity(String accessToken, Long communityId);
//    void changeCommunityBookmarkState(User nowUser, Long nowBoardId);
}
