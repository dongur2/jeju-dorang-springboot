package com.donguri.jejudorang.domain.user.api;

import com.donguri.jejudorang.domain.user.dto.request.PasswordRequest;
import com.donguri.jejudorang.domain.user.dto.request.ProfileRequest;
import com.donguri.jejudorang.domain.user.dto.request.email.MailChangeRequest;
import com.donguri.jejudorang.domain.user.dto.response.ProfileResponse;
import com.donguri.jejudorang.domain.user.service.UserService;
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
@RequestMapping("/profile")
public class ProfileController {

    @Autowired private final UserService userService;

    public ProfileController(UserService userService) {
        this.userService = userService;
    }



    /*
     * 마이페이지 - 프로필 조회
     * GET
     *
     * */
    @GetMapping
    public String getProfileForm(@CookieValue("access_token") Cookie token, Model model) {
        try {
            String accessToken = token.getValue();
            log.info("@CookieValue Cookie's access_token: {}", accessToken);

            ProfileResponse profileData = userService.getProfileData(accessToken);

            log.info("profileResponse.loginType: {}", profileData.loginType());

            model.addAttribute(profileData);
            return "/user/mypage/profile";

        } catch (Exception e) {
            log.error(e.getMessage());
            return "redirect:/user/login";
        }
    }


    /*
     * 마이페이지 - 프로필 수정
     * PUT
     * nickname
     * img: S3 url
     *
     * */
    @PutMapping
    public ResponseEntity<?> updateProfile(@CookieValue("access_token") Cookie token,
                                           @Valid ProfileRequest profileRequest, BindingResult bindingResult) {

        try {
            checkValidationAndReturnException(bindingResult);

            String accessToken = token.getValue();

            userService.updateProfileData(accessToken, profileRequest);
            return new ResponseEntity<>(HttpStatus.OK);

        } catch (Exception e) {
            log.error("프로필 수정 실패: {}", e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }


    /*
     * 마이페이지 - 프로필 사진 삭제
     * DELETE
     *
     * */
    @DeleteMapping("/img")
    public ResponseEntity<HttpStatus> deleteProfileImg(@CookieValue("access_token") Cookie token) {
        log.info("deleteProfileImg 컨트롤러 실행");

        try {
            String accessToken = token.getValue();

            userService.deleteProfileImg(accessToken);

            return new ResponseEntity<>(HttpStatus.OK);

        } catch (Exception e) {
            log.error("이미지 삭제 실패: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }


    /*
     * 이메일 변경
     * PUT
     *
     * */
    @PutMapping("/email")
    public ResponseEntity<?> updateEmail(@CookieValue("access_token") Cookie token,
                                         @RequestBody @Valid MailChangeRequest mailChangeRequest, BindingResult bindingResult) {
        try {
            checkValidationAndReturnException(bindingResult);

            userService.updateEmail(token.getValue(), mailChangeRequest);
            return new ResponseEntity<>(HttpStatus.OK);

        } catch (Exception e) {
            log.error("이메일 변경 실패: {}", e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }


    /*
     * 비밀번호 변경
     * GET, PUT
     *
     * */
    @GetMapping("/pwd")
    public String getUpdatePasswordForm() {
        return "/user/mypage/changePwdForm";
    }

    @PutMapping("/pwd")
    public ResponseEntity<?> updatePassword(@CookieValue("access_token") Cookie token,
                                            @Valid PasswordRequest passwordRequest, BindingResult bindingResult) {

        try {
            checkValidationAndReturnException(bindingResult);

            userService.updatePassword(token.getValue(), passwordRequest);
            return new ResponseEntity<>(HttpStatus.OK);

        } catch (Exception e) {
            log.error("비밀번호 수정 실패: {}", e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
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
