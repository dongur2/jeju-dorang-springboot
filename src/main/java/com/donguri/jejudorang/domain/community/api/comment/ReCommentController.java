package com.donguri.jejudorang.domain.community.api.comment;

import com.donguri.jejudorang.domain.community.dto.request.comment.ReCommentRequest;
import com.donguri.jejudorang.domain.community.dto.request.comment.ReCommentRequestWIthId;
import com.donguri.jejudorang.domain.community.service.comment.ReCommentService;
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
@RequestMapping("/community/comments/re")
public class ReCommentController {

    @Autowired private final ReCommentService reCommentService;

    public ReCommentController(ReCommentService reCommentService) {
        this.reCommentService = reCommentService;
    }


    /*
    * 대댓글 작성
    * POST
    *
    * */
    @PostMapping
    public String createNewReComment(@CookieValue("access_token") Cookie token,
                                     @Valid ReCommentRequest request, BindingResult bindingResult,
                                     @RequestParam("type") String type, Model model) {
        try {
            if (bindingResult.hasErrors()) {
                throw new Exception(bindingResult.getFieldError().getDefaultMessage());
            }

            reCommentService.writeNewReComment(token.getValue(), request);

            // PARTY -> parties, CHAT -> chats
            type = matchMappingBoardType(type);

            return "redirect:/community/boards/" + type + "/" + request.postId();

        } catch (Exception e) {
            log.error("대댓글 작성에 실패했습니다. {}", e.getMessage());
            model.addAttribute("errorMsg", e.getMessage());
            return "/error/errorTemp";
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
     * 대댓글 수정
     * PUT
     *
     * */
    @PutMapping
    public ResponseEntity<?> updateReComment(@CookieValue("access_token") Cookie token,
                                             @RequestBody @Valid ReCommentRequestWIthId reCommentRequest, BindingResult bindingResult) {
        try {
            if(bindingResult.hasErrors()) {
                throw new Exception(bindingResult.getFieldError().getDefaultMessage());
            }

            reCommentService.modifyReComment(token.getValue(), reCommentRequest);

            return new ResponseEntity<>(HttpStatus.OK);

        } catch (Exception e) {
            log.error("대댓글 수정 실패: {}", e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

}
