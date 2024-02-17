package com.donguri.jejudorang.domain.bookmark.service;


import com.donguri.jejudorang.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Map;

public interface BookmarkService {

    // 북마크 생성
    void addBookmark(String accessToken, String boardName, Long boardId);

    // 북마크 삭제
    void deleteBookmark(String accessToken, String boardName, Long boardId);

    // 북마크 조회
    Page<?> getMyBookmarks(User user, String type, Pageable pageable);


    // 회원 탈퇴 시 그 회원의 모든 북마크 삭제
    void deleteAllBookmarksOfUser(Long userId);

}
