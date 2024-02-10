package com.donguri.jejudorang.domain.user.api;

import com.donguri.jejudorang.domain.community.dto.response.CommunityListResponseDto;
import com.donguri.jejudorang.domain.user.dto.request.*;
import com.donguri.jejudorang.domain.user.dto.request.email.*;
import com.donguri.jejudorang.domain.user.dto.response.ProfileResponse;
import com.donguri.jejudorang.domain.user.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;


@Slf4j
@Controller
@RequestMapping("/user")
public class User2Controller {



    @Autowired private final UserService userService;
    public User2Controller(UserService userService) {
        this.userService = userService;
    }





    /*
    * 이메일 인증 번호 전송 (+ 중복 확인)
    * */
    @PostMapping("/email/verify")
    public ResponseEntity<?> checkDuplicatedAndSendEmailCode(@RequestBody @Valid MailSendRequest mailSendRequest, BindingResult bindingResult) {
        try {
            checkValidationAndReturnException(bindingResult);

            userService.checkMailDuplicatedAndSendVerifyCode(mailSendRequest);

            log.info("이메일 인증 번호 전송 완료");
            return new ResponseEntity<>(HttpStatus.OK);

        } catch (NullPointerException e) {
            log.error("이메일 인증 번호 전송 실패: {}", e.getMessage());
            return new ResponseEntity<>("이메일을 입력해주세요.", HttpStatus.BAD_REQUEST);

        } catch (Exception e) {
            log.error("이메일 인증 번호 전송 실패: {}", e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /*
    * 이메일 인증 번호 확인
    * */
    @PostMapping("/email/verify-check")
    public ResponseEntity<?> checkEmailCode(@RequestBody @Valid MailVerifyRequest mailVerifyRequest, BindingResult bindingResult) {
        try {
            checkValidationAndReturnException(bindingResult);

            boolean checkRes = userService.checkVerifyMail(mailVerifyRequest);
            if (checkRes) {
                log.info("이메일 인증 완료 : 인증 번호 일치");
                return new ResponseEntity<>(HttpStatus.OK);

            } else {
                log.error("이메일 인증 실패 : 인증 번호 불일치");
                return new ResponseEntity<>("인증 번호가 일치하지 않습니다", HttpStatus.BAD_REQUEST);
            }

        } catch (NullPointerException e) {
            log.error("이메일 인증 실패: {}", e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);

        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_GATEWAY);
        }
    }





















    /*
    * 아이디 찾기 이메일 전송
    * */
    @PostMapping("/email/find-id")
    public ResponseEntity<?> findId(@RequestBody @Valid MailSendRequest mailSendRequest, BindingResult bindingResult) {
        try {
            checkValidationAndReturnException(bindingResult);

            userService.sendMailWithId(mailSendRequest);
            return new ResponseEntity<>(HttpStatus.OK);

        } catch (Exception e) {
            log.error("아이디 찾기 실패: {}", e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /*
    * 비밀번호 찾기 이메일 전송
    * */
    @PostMapping("/email/find-pwd")
    public ResponseEntity<?> findPwd(@RequestBody @Valid MailSendForPwdRequest mailSendForPwdRequest, BindingResult bindingResult) {
        try {
            checkValidationAndReturnException(bindingResult);

            userService.checkUserAndSendVerifyCode(mailSendForPwdRequest);
            return new ResponseEntity<>(HttpStatus.OK);

        } catch (Exception e) {
            log.error("아이디 찾기 실패: {}", e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }


    /*
    * 랜덤 비밀번호 설정: 이메일 전송
    * */
    @PostMapping("/email/change-pwd")
    public ResponseEntity<?> changePwd(@RequestBody @Valid MailSendForPwdRequest mailSendForPwdRequest, BindingResult bindingResult) {
        try {
            checkValidationAndReturnException(bindingResult);

            userService.changePwdRandomlyAndSendMail(mailSendForPwdRequest);
            return new ResponseEntity<>(HttpStatus.OK);

        } catch (Exception e) {
            log.error("비밀번호 재설정 실패: {}", e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }




    /*
     * 마이페이지 - 작성글 목록: 커뮤니티
     *
     * */
    @GetMapping("/settings/profile/writings")
    public String getMyWritingsPage(@CookieValue("access_token") Cookie token, Model model,
                                    @RequestParam(name = "page", required = false, defaultValue = "0") Integer nowPage) {
        try {
            Pageable pageable = PageRequest.of(nowPage, 10, Sort.by("createdAt").descending());

            Page<CommunityListResponseDto> data = userService.getMyCommunityWritings(token.getValue(), pageable);

            model.addAttribute("nowPage", nowPage);
            model.addAttribute("endPage", data.getTotalPages());
            model.addAttribute("posts", data);
            return "/user/mypage/myWritings";

        } catch (Exception e) {
            log.error("커뮤니티 작성글 불러오기 실패: {}", e.getMessage());
            model.addAttribute("errorMsg", e.getMessage());
            return "/error/errorTemp";
        }
    }

    /*
    * 마이페이지 - 북마크 목록: 여행/커뮤니티
    *
    * */
    @GetMapping("/settings/profile/bookmarks")
    public String getMyBookmarkPage(@CookieValue("access_token") Cookie token, Model model,
                                    @RequestParam(name = "type", required = false, defaultValue = "trip") String type,
                                    @RequestParam(name = "page", required = false, defaultValue = "0") Integer nowPage) {
        try {
            Pageable pageable = PageRequest.of(nowPage, 10, Sort.by("createdAt").descending());
            Page<?> data = userService.getMyBookmarks(token.getValue(), type, pageable);

            model.addAttribute("type", type);
            model.addAttribute("nowPage", nowPage);
            model.addAttribute("endPage", data.getTotalPages());
            model.addAttribute("posts", data);
            return "/user/mypage/myBookmarks";

        } catch (Exception e) {
            log.error("북마크 불러오기 실패: {}", e.getMessage());
            model.addAttribute("errorMsg", e.getMessage());
            return "/error/errorTemp";
        }
    }


    /*
    * DTO Validation 에러 체크 후 에러 발생시에러 메세지 세팅한 Exception throw
    * */
    private static void checkValidationAndReturnException(BindingResult bindingResult) throws Exception {
        if (bindingResult.hasErrors()) {
            log.error("실패: {}", bindingResult.getFieldError().getDefaultMessage());
            throw new Exception(bindingResult.getFieldError().getDefaultMessage());
        }
    }



}
