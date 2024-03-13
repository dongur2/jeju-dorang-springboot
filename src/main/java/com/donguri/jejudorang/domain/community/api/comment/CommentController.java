package com.donguri.jejudorang.domain.community.api.comment;

import com.donguri.jejudorang.domain.community.api.comment.swagger.CommentControllerDocs;
import com.donguri.jejudorang.domain.community.dto.request.comment.CommentRequest;
import com.donguri.jejudorang.domain.community.dto.request.comment.CommentRequestWithId;
import com.donguri.jejudorang.domain.community.dto.request.comment.ReCommentRequest;
import com.donguri.jejudorang.domain.community.service.comment.CommentService;
import com.donguri.jejudorang.global.error.CustomException;
import jakarta.servlet.http.Cookie;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/community/comments")
public class CommentController implements CommentControllerDocs {
    private final CommentService commentService;


    /*
    * 댓글 작성
    * POST
    *
    * */
    @PostMapping
    public ResponseEntity<?> createNewComment(@CookieValue("access_token") Cookie token,
                                               @Valid CommentRequest commentRequest, BindingResult bindingResult,
                                               @RequestParam("type") String type) {
        try {
            if (bindingResult.hasErrors()) {
                return new ResponseEntity<>(bindingResult.getFieldError().getDefaultMessage(), HttpStatus.BAD_REQUEST);
            }

            commentService.writeNewComment(token.getValue(), commentRequest);

            // PARTY -> parties, CHAT -> chats
            type = matchMappingBoardType(type);

            return new ResponseEntity<>(HttpStatus.OK);

        } catch (CustomException e) {
            log.error("댓글 작성에 실패했습니다. {}", e.getCustomErrorCode().getMessage());
            return new ResponseEntity<>(e.getCustomErrorCode().getMessage(), e.getCustomErrorCode().getStatus());

        } catch (Exception e) {
            log.error("댓글 생성에 실패했습니다: [서버 오류] {}", e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    /*
     * 대댓글 작성
     * POST
     *
     * */
    @PostMapping("/re")
    public ResponseEntity<?> createNewReComment(@CookieValue("access_token") Cookie token,
                                                 @Valid ReCommentRequest request, BindingResult bindingResult,
                                                 @RequestParam("type") String type) {
        try {
            if (bindingResult.hasErrors()) {
                return new ResponseEntity<>(bindingResult.getFieldError().getDefaultMessage(), HttpStatus.BAD_REQUEST);
            }

            commentService.writeNewReComment(token.getValue(), request);

            // PARTY -> parties, CHAT -> chats
            type = matchMappingBoardType(type);

            return new ResponseEntity<>(HttpStatus.OK);

        } catch (CustomException e) {
            log.error("대댓글 작성에 실패했습니다. {}", e.getCustomErrorCode().getMessage());
            return new ResponseEntity<>(e.getCustomErrorCode().getMessage(), e.getCustomErrorCode().getStatus());

        } catch (Exception e) {
            log.error("대댓글 작성에 실패했습니다. [서버 오류] {}", e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
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
            if (bindingResult.hasErrors()) {
                return new ResponseEntity<>(bindingResult.getFieldError().getDefaultMessage(), HttpStatus.BAD_REQUEST);
            }

            commentService.modifyComment(token.getValue(), commentRequest);

            return new ResponseEntity<>(HttpStatus.OK);

        } catch (CustomException e) {
            log.error("댓글 수정 실패: {}", e.getCustomErrorCode().getMessage());
            return new ResponseEntity<>(e.getCustomErrorCode().getMessage(), e.getCustomErrorCode().getStatus());

        } catch (Exception e) {
            log.error("댓글 수정 실패: [서버 오류] {}", e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
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

        } catch (CustomException e) {
            log.error("댓글 삭제 실패: {}", e.getCustomErrorCode().getMessage());
            return new ResponseEntity<>(e.getCustomErrorCode().getMessage(), e.getCustomErrorCode().getStatus());

        } catch (Exception e) {
            log.error("댓글 삭제 실패: [서버 오류] {}", e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
