package com.donguri.jejudorang.domain.bookmark.api;

import com.donguri.jejudorang.domain.bookmark.service.BookmarkService;
import com.donguri.jejudorang.global.error.CustomException;
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


    /*
    * 북마크 생성
    * /bookmarks
    * POST
    *
    * */
    @PostMapping
    public ResponseEntity<String> createBookmark(@CookieValue("access_token") Cookie accessToken,
                                                  @RequestParam("type") String type,
                                                  @RequestParam("id") Long postId) {

        try {
            bookmarkService.addBookmark(accessToken.getValue(), type, postId);

            log.info("북마크 설정 완료: {}", postId);
            return new ResponseEntity<>("북마크가 설정되었습니다", HttpStatus.OK);

        } catch (CustomException e) {
            log.error("북마크 설정 실패: {}", e.getCustomErrorCode().getMessage());
            return new ResponseEntity<>(e.getCustomErrorCode().getMessage(), e.getCustomErrorCode().getStatus());
        }
    }


    /*
    * 북마크 삭제
    * /bookmarks
    * DELETE
    *
    * @param type: trip/community
    *
    *  */
    @DeleteMapping
    public ResponseEntity<String> deleteBookmark(@CookieValue("access_token") Cookie accessToken,
                                                 @RequestParam("type") String type,
                                                 @RequestParam("id") Long postId) {

        try {
            bookmarkService.deleteBookmark(accessToken.getValue(), type, postId);
            return new ResponseEntity<>("북마크 해제가 완료되었습니다.", HttpStatus.OK);

        } catch (CustomException e) {
            log.error("북마크 해제 실패: {}", e.getMessage());
            return new ResponseEntity<>(e.getCustomErrorCode().getMessage(), e.getCustomErrorCode().getStatus());
        }
    }

    /*
    * 이미 삭제된 글의 북마크 삭제
    * 커뮤니티만
    * */
    @DeleteMapping("/deleted")
    public ResponseEntity<String> deleteCommunityBookmarkDirectly(@CookieValue("access_token") Cookie accessToken,
                                                                  @RequestParam("id") Long bookmarkId) {
        try {
            bookmarkService.deleteCommunityBookmarkOnDeletedPost(accessToken.getValue(), bookmarkId);
            return new ResponseEntity<>("북마크 해제가 완료되었습니다.", HttpStatus.OK);

        } catch (CustomException e) {
            log.error("북마크 해제 실패: {}", e.getMessage());
            return new ResponseEntity<>(e.getCustomErrorCode().getMessage(), e.getCustomErrorCode().getStatus());
        }
    }

}
