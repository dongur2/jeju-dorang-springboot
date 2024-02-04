package com.donguri.jejudorang.domain.bookmark.api;

import com.donguri.jejudorang.domain.bookmark.service.BookmarkService;
import jakarta.servlet.http.Cookie;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/bookmarks")
public class BookmarkController {
    @Autowired private final BookmarkService bookmarkService;
    public BookmarkController(BookmarkService bookmarkService) {
        this.bookmarkService = bookmarkService;
    }

    @PostMapping("/communities/{communityId}")
    public ResponseEntity<String> createCommunityBookmark(@CookieValue("access_token") Cookie accessToken,
                                                          @PathVariable("communityId") Long communityId) {

        try {
            bookmarkService.makeBookmarkOnCommunity(accessToken.getValue(), communityId);
            log.info("북마크 완료: {}", communityId);
            return new ResponseEntity<>("북마크가 완료되었습니다", HttpStatus.OK);

        } catch (Exception e) {
            log.error("북마크 생성 실패: {}", e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }


}
