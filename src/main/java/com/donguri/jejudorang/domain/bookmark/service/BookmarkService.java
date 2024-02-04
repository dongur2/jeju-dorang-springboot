package com.donguri.jejudorang.domain.bookmark.service;



public interface BookmarkService {

    // 북마크 생성
    void addBookmark(String accessToken, String boardName, Long boardId);

    // 북마크 삭제
    void deleteBookmark(String accessToken, String boardName, Long boardId);

}
