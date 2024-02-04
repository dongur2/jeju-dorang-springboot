package com.donguri.jejudorang.domain.bookmark.service;



public interface BookmarkService {

    // 북마크 생성
    void addBookmark(String accessToken, String boardName, Long communityId);

    // 북마크 삭제
    void deleteBookmarkOnCommunity(String accessToken, Long communityId);

}
