package com.donguri.jejudorang.domain.user.api;

import com.donguri.jejudorang.domain.user.dto.request.*;
import com.donguri.jejudorang.domain.user.dto.request.email.*;
import com.donguri.jejudorang.domain.user.dto.response.ProfileResponse;
import com.donguri.jejudorang.domain.user.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;


@Slf4j
@Controller
@RequestMapping("/user")
public class UserController {

    @Value("${jwt.cookie-expire}")
    private int cookieTime;

    @Autowired private final UserService userService;
    public UserController(UserService userService) {
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
    * 회원 가입
    * */
    @GetMapping("/signup")
    public String registerForm() {
        return "/user/login/signUpForm";
    }
    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid SignUpRequest signUpRequest, BindingResult bindingResult) {

        try {
            checkValidationAndReturnException(bindingResult);

            userService.signUp(signUpRequest);
            log.info("회원 가입 완료");
            return new ResponseEntity<>(HttpStatus.OK);

        } catch (Exception e) {
            log.error("회원 가입 실패");
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_GATEWAY);
        }
    }

    @GetMapping("/login")
    public String signInForm() {
        return "/user/login/signInForm";
    }
    @PostMapping("/login")
    public String authenticateUser(@Valid LoginRequest loginRequest, BindingResult bindingResult,
                                   HttpServletResponse response, Model model) {
        // 유효성 검사 에러
        if (bindingResult.hasErrors()) {
            return bindErrorPage(bindingResult, model);
        }

        try {
            Map<String, String> tokens = userService.signIn(loginRequest);

            if (tokens == null || tokens.get("access_token") == null) {
                log.error("해당 아이디의 비밀번호가 올바르지 않습니다: {}", loginRequest.externalId());
                model.addAttribute("errorMsg", "비밀번호를 확인해주세요");
                return "/user/login/signInForm";

            } else {
                setCookieForToken(tokens, response);
                return "redirect:/";
            }

        } catch (UnexpectedRollbackException e) {
            log.error("가입된 아이디가 아닙니다 : {}", e.getMessage());
            model.addAttribute("errorMsg", "가입된 아이디가 없습니다");
            return "/user/login/signInForm";

        } catch (Exception e) {
            log.error("로그인에 실패했습니다: {}", e.getMessage());
            model.addAttribute("errorMsg", e.getMessage());
            return "/user/login/signInForm";
        }
    }

    private void setCookieForToken(Map<String, String> tokens, HttpServletResponse response) {
        tokens.forEach((index, token) -> {
            Cookie newCookieToAdd = new Cookie(index, token);
            newCookieToAdd.setHttpOnly(true);
            newCookieToAdd.setMaxAge(cookieTime);
            newCookieToAdd.setPath("/");
            response.addCookie(newCookieToAdd);
        });
    }

    /*
    *  SecurityConfig의 securityFilterChain() .logout() 설정
    *  으로 인해서 이 컨트롤러 메서드는 실행되지 않음
    * */
    @PostMapping("/logout")
    public String deleteUser(HttpServletResponse response) {
        Optional<Authentication> authState = userService.logOut();

        if (authState.isPresent()) {
            return "/error/errorTemp";
        } else {
            return "redirect:/";
        }
    }


    /*
    * 마이페이지 - 프로필 조회
    * */
    @GetMapping("/settings/profile")
    public String getProfileForm(@CookieValue("access_token") Cookie token, Model model) {
        try {
            String accessToken = token.getValue();
            log.info("@CookieValue Cookie's access_token: {}", accessToken);

            ProfileResponse profileData = userService.getProfileData(accessToken);

            model.addAttribute(profileData);
            return "/user/mypage/profile";

        } catch (Exception e) {
            log.error(e.getMessage());
            return "redirect:/user/login";
        }
    }

    /*
    * 마이페이지 - 프로필 수정
    * nickname
    * img: S3 url
    *
    * */
    @PutMapping("/settings/profile")
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
    * */
    @DeleteMapping("/settings/profile/deleteimg")
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
    * 비밀번호 수정
    * */
    @GetMapping("/settings/profile/pwd")
    public String getUpdatePasswordForm() {
        return "/user/mypage/changePwdForm";
    }
    @PutMapping("/settings/profile/pwd")
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
    * 이메일 수정
    * */
    @PutMapping("/email/change")
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
    * 회원 탈퇴
    *
    * */
    @PostMapping("/settings/profile/quit")
    public ResponseEntity<?> deleteUser(@CookieValue("access_token") Cookie token) {
        try {
            userService.withdrawUser(token.getValue());
            return new ResponseEntity<>(HttpStatus.OK);

        } catch (Exception e) {
            log.error("회원 삭제 실패: {}", e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /*
     * 마이페이지 - 작성글 목록: 커뮤니티
     *
     * */
    @GetMapping("/settings/profile/writings")
    public String getMyWritingsPage(@CookieValue("access_token") Cookie token, Model model,
                                    @RequestParam(name = "nowPage", required = false, defaultValue = "0") Integer nowPage) {
        try {
            Pageable pageable = PageRequest.of(nowPage, 10);
            Map<String, Object> resultMap = userService.getMyCommunityWritings(token.getValue(), pageable);

            model.addAttribute("endPage", resultMap.get("pageCount"));
            model.addAttribute("post", resultMap.get("data"));
            return "/user/mypage/myWritings";

        } catch (Exception e) {
            log.error("커뮤니티 작성글 불러오기 실패: {}", e.getMessage());
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


    private static String bindErrorPage(BindingResult bindingResult, Model model) {
        model.addAttribute("errorMsg", bindingResult.getFieldError().getDefaultMessage());
        return "/error/errorTemp";
    }
}
