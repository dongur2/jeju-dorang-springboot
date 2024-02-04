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

    @PostMapping("/{boardName}/{boardId}")
    public ResponseEntity<String> createCommunityBookmark(@CookieValue("access_token") Cookie accessToken,
                                                          @PathVariable("boardName") String boardName,
                                                          @PathVariable("boardId") Long boardId) {

        try {
            bookmarkService.addBookmark(accessToken.getValue(), boardName, boardId);

            log.info("북마크 설정 완료: {}", boardId);
            return new ResponseEntity<>("북마크가 설정되었습니다", HttpStatus.OK);

        } catch (Exception e) {
            log.error("북마크 설정 실패: {}", e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/communities/{communityId}")
    public ResponseEntity<String> deleteCommunityBookmark(@CookieValue("access_token") Cookie accessToken,
                                                          @PathVariable("communityId") Long communityId) {

        try {
            bookmarkService.deleteBookmarkOnCommunity(accessToken.getValue(), communityId);
            log.info("북마크 해제 완료");
            return new ResponseEntity<>("북마크 해제가 완료되었습니다.", HttpStatus.OK);

        } catch (Exception e) {
            log.error("북마크 해제 실패: {}", e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }




}
