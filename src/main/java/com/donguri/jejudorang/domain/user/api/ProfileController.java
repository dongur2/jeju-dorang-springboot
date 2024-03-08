package com.donguri.jejudorang.domain.user.api;

import com.donguri.jejudorang.domain.user.api.swagger.ProfileControllerDocs;
import com.donguri.jejudorang.domain.user.dto.request.PasswordRequest;
import com.donguri.jejudorang.domain.user.dto.request.ProfileRequest;
import com.donguri.jejudorang.domain.user.dto.request.email.MailChangeRequest;
import com.donguri.jejudorang.domain.user.dto.response.ProfileResponse;
import com.donguri.jejudorang.domain.user.service.UserService;
import com.donguri.jejudorang.global.error.CustomException;
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
public class ProfileController implements ProfileControllerDocs {

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

            ProfileResponse profileData = userService.getProfileData(accessToken);

            model.addAttribute(profileData);
            return "user/mypage/profile";

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
            if (bindingResult.hasErrors()) {
                return new ResponseEntity<>(bindingResult.getFieldError().getDefaultMessage(), HttpStatus.BAD_REQUEST);
            }

            String accessToken = token.getValue();

            userService.updateProfileData(accessToken, profileRequest);
            return new ResponseEntity<>(HttpStatus.OK);

        } catch (CustomException e) {
            log.error("프로필 수정 실패: [오류] {}", e.getCustomErrorCode().getMessage());
            return new ResponseEntity<>(e.getCustomErrorCode().getMessage(), e.getCustomErrorCode().getStatus());

        } catch (Exception e) {
            log.error("프로필 수정 실패: [서버 오류] {}", e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    /*
     * 마이페이지 - 프로필 사진 삭제
     * DELETE
     *
     * */
    @DeleteMapping("/img")
    public ResponseEntity<HttpStatus> deleteProfileImg(@CookieValue("access_token") Cookie token) {
        try {
            String accessToken = token.getValue();

            userService.deleteProfileImg(accessToken);

            return new ResponseEntity<>(HttpStatus.OK);

        } catch (Exception e) {
            log.error("이미지 삭제 실패: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
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
            if (bindingResult.hasErrors()) {
                return new ResponseEntity<>(bindingResult.getFieldError().getDefaultMessage(), HttpStatus.BAD_REQUEST);
            }

            userService.updateEmail(token.getValue(), mailChangeRequest);
            return new ResponseEntity<>(HttpStatus.OK);

        } catch (Exception e) {
            log.error("이메일 변경 실패: {}", e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    /*
     * 비밀번호 변경
     * GET, PUT
     *
     * */
    @GetMapping("/pwd")
    public String getUpdatePasswordForm(@CookieValue("access_token") Cookie token, Model model) {
        String loginType = userService.checkLoginType(token.getValue());

        model.addAttribute("loginType", loginType);
        return "user/mypage/changePwdForm";
    }

    @PutMapping("/pwd")
    public ResponseEntity<?> updatePassword(@CookieValue("access_token") Cookie token,
                                            @Valid PasswordRequest passwordRequest, BindingResult bindingResult) {

        try {
            if (bindingResult.hasErrors()) {
                return new ResponseEntity<>(bindingResult.getFieldError().getDefaultMessage(), HttpStatus.BAD_REQUEST);
            }

            userService.updatePassword(token.getValue(), passwordRequest);
            return new ResponseEntity<>(HttpStatus.OK);

        } catch (CustomException e) {
            log.error("비밀번호 변경 실패: [오류] {}", e.getCustomErrorCode().getMessage());
            return new ResponseEntity<>(e.getCustomErrorCode().getMessage(), e.getCustomErrorCode().getStatus());

        } catch (Exception e) {
            log.error("비밀번호 수정 실패: [서버 오류] {}", e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
