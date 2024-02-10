package com.donguri.jejudorang.domain.community.api.comment;

import com.donguri.jejudorang.domain.community.dto.request.comment.CommentRequest;
import com.donguri.jejudorang.domain.community.service.CommunityService;
import com.donguri.jejudorang.domain.community.service.comment.CommentService;
import jakarta.servlet.http.Cookie;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@Controller
@RequestMapping("/community/comments")
public class CommentController {

    @Autowired private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping("/new")
    public ResponseEntity<?> createNewComment(@CookieValue("access_token") Cookie token,
                                   @RequestParam("post") Long postId,
                                   @Valid CommentRequest commentRequest, BindingResult bindingResult) {
        try {
            if (bindingResult.hasErrors()) {
                throw new Exception(bindingResult.getFieldError().getDefaultMessage());
            }

            commentService.writeNewComment(token.getValue(), postId, commentRequest);
            return new ResponseEntity<>(HttpStatus.OK);

        } catch (Exception e) {
            log.error("댓글 생성에 실패했습니다: {}", e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }


}
