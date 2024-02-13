package com.donguri.jejudorang.domain.community.api.comment;

import com.donguri.jejudorang.domain.community.dto.request.comment.CommentRequest;
import com.donguri.jejudorang.domain.community.dto.request.comment.CommentRequestWithId;
import com.donguri.jejudorang.domain.community.dto.request.comment.ReCommentRequest;
import com.donguri.jejudorang.domain.community.service.comment.CommentService;
import jakarta.servlet.http.Cookie;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Controller
@RequestMapping("/community/comments")
public class CommentController {

    @Autowired private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }


    /*
    * 댓글 작성
    * POST
    *
    * */
    @PostMapping
    public String createNewComment(@CookieValue("access_token") Cookie token,
                                   @Valid CommentRequest commentRequest, BindingResult bindingResult,
                                   @RequestParam("type") String type,
                                   Model model) {
        try {
            if (bindingResult.hasErrors()) {
                throw new Exception(bindingResult.getFieldError().getDefaultMessage());
            }

            commentService.writeNewComment(token.getValue(), commentRequest);

            // PARTY -> parties, CHAT -> chats
            type = matchMappingBoardType(type);

            return "redirect:/community/boards/" + type + "/" + commentRequest.postId();

        } catch (Exception e) {
            log.error("댓글 생성에 실패했습니다: {}", e.getMessage());
            model.addAttribute("errorMsg", e.getMessage());
            return "errorPage";
        }
    }


    /*
     * 대댓글 작성
     * POST
     *
     * */
    @PostMapping("/re")
    public String createNewReComment(@CookieValue("access_token") Cookie token,
                                     @Valid ReCommentRequest request, BindingResult bindingResult,
                                     @RequestParam("type") String type, Model model) {
        try {
            if (bindingResult.hasErrors()) {
                throw new Exception(bindingResult.getFieldError().getDefaultMessage());
            }

            commentService.writeNewReComment(token.getValue(), request);

            // PARTY -> parties, CHAT -> chats
            type = matchMappingBoardType(type);

            return "redirect:/community/boards/" + type + "/" + request.postId();

        } catch (Exception e) {
            log.error("대댓글 작성에 실패했습니다. {}", e.getMessage());
            model.addAttribute("errorMsg", e.getMessage());
            return "errorPage";
        }
    }


    private static String matchMappingBoardType(String type) {
        if(type.equals("PARTY")) {
            type = "parties";
        } else {
            type = "chats";
        }
        return type;
    }


    /*
    * 댓글 수정
    * PUT
    *
    * */
    @PutMapping
    public ResponseEntity<?> updateComment(@CookieValue("access_token") Cookie token,
                                           @RequestBody @Valid CommentRequestWithId commentRequest, BindingResult bindingResult) {

        try {
            if(bindingResult.hasErrors()) {
                throw new Exception(bindingResult.getFieldError().getDefaultMessage());
            }

            commentService.modifyComment(token.getValue(), commentRequest);

            return new ResponseEntity<>(HttpStatus.OK);

        } catch (Exception e) {
            log.error("댓글 수정 실패: {}", e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }


    /*
    * 댓글 삭제
    * DELETE
    *
    * */
    @DeleteMapping
    public ResponseEntity<?> deleteComment(@CookieValue("access_token") Cookie token, @RequestParam("cmtId") Long cmtId) {
        try {
            commentService.deleteComment(token.getValue(), cmtId);

            return new ResponseEntity<>(HttpStatus.OK);

        } catch (Exception e) {
            log.error("댓글 삭제 실패: {}", e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }




}
