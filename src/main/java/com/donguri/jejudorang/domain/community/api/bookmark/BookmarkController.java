package com.donguri.jejudorang.domain.community.api.bookmark;

import com.donguri.jejudorang.domain.community.service.bookmark.BookmarkService;
import com.donguri.jejudorang.domain.user.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class BookmarkController {
    @Autowired
    private BookmarkService bookmarkService;

//    @PutMapping("/{communityId}/bookmark")
//    public ResponseEntity<String> updateCommunityLikedState(@AuthenticationPrincipal User nowUser,
//                                                            @PathVariable("communityId") Long communityId) {
//        if (nowUser == null) {
//            log.info("nowUser is NULL");
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("비회원은 북마크 불가능");
//        } else {
//            log.info("nowUser={}", nowUser.toString());
//            bookmarkService.changeCommunityLikedState(nowUser.getNum(), communityId);
//            return ResponseEntity.ok("북마크 완료");
//        }
//    }
}
