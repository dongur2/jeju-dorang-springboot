package com.donguri.jejudorang.domain.bookmark.service;



public interface BookmarkService {

    void makeBookmarkOnCommunity(String accessToken, Long communityId);
//    void changeCommunityBookmarkState(User nowUser, Long nowBoardId);
}
