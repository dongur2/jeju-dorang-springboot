package com.donguri.jejudorang.domain.bookmark.service;


import com.donguri.jejudorang.domain.user.entity.User;
import org.springframework.data.domain.Pageable;

import java.util.Map;

public interface BookmarkService {

    // 북마크 생성
    void addBookmark(String accessToken, String boardName, Long boardId);

    // 북마크 삭제
    void deleteBookmark(String accessToken, String boardName, Long boardId);

    // 북마크 조회
    Map<String, Object> getMyBookmarks(User user, String type, Pageable pageable);

}
