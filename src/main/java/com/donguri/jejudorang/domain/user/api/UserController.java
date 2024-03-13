package com.donguri.jejudorang.domain.user.api;

import com.donguri.jejudorang.domain.user.api.swagger.UserControllerDocs;
import com.donguri.jejudorang.domain.user.dto.request.LoginRequest;
import com.donguri.jejudorang.domain.user.dto.request.SignUpRequest;
import com.donguri.jejudorang.domain.user.service.UserService;
import com.donguri.jejudorang.global.error.CustomException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@Controller
@RequestMapping("/user")
public class UserController implements UserControllerDocs {
    private final int cookieTime;
    private final String kakaoApiKey;
    private final UserService userService;

    @Autowired
    public UserController(@Value("${jwt.cookie-expire}") int cookieTime, @Value("${kakao.key}") String kakaoApiKey, UserService userService) {
        this.cookieTime = cookieTime;
        this.kakaoApiKey = kakaoApiKey;
        this.userService = userService;
    }


    /*
     * 회원 가입
     * */
    @GetMapping("/signup")
    public String registerForm() {
        return "user/login/signUpForm";
    }
    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid SignUpRequest signUpRequest, BindingResult bindingResult) {
        try {
            if (bindingResult.hasErrors()) {
                return new ResponseEntity<>(bindingResult.getFieldError().getDefaultMessage(), HttpStatus.BAD_REQUEST);
            }

            userService.signUp(signUpRequest);
            log.info("회원 가입 완료");
            return new ResponseEntity<>(HttpStatus.OK);

        } catch (CustomException e) {
            log.error("CUSTOM 회원 가입 실패: {}", e.getCustomErrorCode().getMessage());
            return new ResponseEntity<>(e.getCustomErrorCode().getMessage(), e.getCustomErrorCode().getStatus());

        } catch (Exception e) {
            log.error("회원 가입 실패: {}", e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    /*
    * 로그인
    * GET, POST
    *
    * */
    @GetMapping("/login")
    public String signInForm(Model model) {
        model.addAttribute("kakaoApiKey", kakaoApiKey);
        return "user/login/signInForm";
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid LoginRequest loginRequest, BindingResult bindingResult,
                                   HttpServletResponse response) {
        // 유효성 검사 에러
        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>(bindingResult.getFieldError().getDefaultMessage(), HttpStatus.BAD_REQUEST);
        }

        try {
            Map<String, String> tokens = userService.signIn(loginRequest);

            if (tokens == null || tokens.get("access_token") == null) {
                log.error("해당 아이디의 비밀번호가 올바르지 않습니다: {}", loginRequest.externalId());
                return new ResponseEntity<>("비밀번호를 확인해주세요", HttpStatus.UNAUTHORIZED);

            } else {
                setCookieForToken(tokens, response);
                return new ResponseEntity<>(HttpStatus.OK);
            }

        } catch (UnexpectedRollbackException e) {
            log.error("가입된 아이디가 아닙니다 : {}", e.getMessage());
            return new ResponseEntity<>("가입된 아이디가 없습니다", HttpStatus.NOT_FOUND);

        } catch (Exception e) {
            log.error("로그인에 실패했습니다: {}", e.getMessage());
            return new ResponseEntity<>("로그인에 실패했습니다", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    /*
     * 회원 탈퇴
     * POST
     *
     * */
    @PostMapping("/quit")
    public ResponseEntity<?> deleteUser(@CookieValue("access_token") Cookie token, @RequestParam("type") String loginType) {
        try {
            if(loginType.equals("BASIC")) {
                userService.withdrawUser(token.getValue());
            } else {
                userService.withdrawKakaoUser(token.getValue());
            }

            return new ResponseEntity<>(HttpStatus.OK);

        } catch (CustomException e) {
            log.error("회원 삭제 실패: {}", e.getCustomErrorCode().getMessage());
            return new ResponseEntity<>(e.getCustomErrorCode().getMessage(), e.getCustomErrorCode().getStatus());

        } catch (Exception e) {
            log.error("회원 삭제 실패 [서버 오류] {}", e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    // 토큰을 쿠키에 저장
    private void setCookieForToken(Map<String, String> tokens, HttpServletResponse response) {
        tokens.forEach((index, token) -> {
            Cookie newCookieToAdd = new Cookie(index, token);
            newCookieToAdd.setHttpOnly(true);
            newCookieToAdd.setMaxAge(cookieTime);
            newCookieToAdd.setPath("/");
            response.addCookie(newCookieToAdd);
        });
    }

}
