package com.donguri.jejudorang.domain.board.api;

import com.donguri.jejudorang.domain.board.entity.Liked;
import com.donguri.jejudorang.domain.board.service.LikedService;
import com.donguri.jejudorang.domain.user.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class LikedController {
    @Autowired
    private LikedService likedService;

    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/board/detail/{boardId}/updateLiked")
    public void updateBoardLikedState(@AuthenticationPrincipal User nowUser,
                                       @PathVariable("boardId") Long boardId) {
        if (nowUser == null) {
            log.info("nowUser is NULL");
        } else {
            log.info("nowUser={}", nowUser.toString());
            likedService.updateBoardLikedState(nowUser.getId(), boardId);
        }
    }
}
