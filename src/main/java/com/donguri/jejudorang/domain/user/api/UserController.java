package com.donguri.jejudorang.domain.user.api;

import com.donguri.jejudorang.domain.user.dto.request.LoginRequest;
import com.donguri.jejudorang.domain.user.dto.request.SignUpRequest;
import com.donguri.jejudorang.domain.user.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    private final int cookieTime;
    private final String kakaoApiKey;
    @Autowired private final UserService userService;

    public UserController(@Value("${jwt.cookie-expire}") int cookieTime, UserService userService, @Value("${kakao.key}") String kakaoApiKey) {
        this.cookieTime = cookieTime;
        this.userService = userService;
        this.kakaoApiKey = kakaoApiKey;
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


    /*
    * 로그인
    * GET, POST
    *
    * */
    @GetMapping("/login")
    public String signInForm(Model model) {
        model.addAttribute("kakaoApiKey", kakaoApiKey);
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


    /*
     *  로그아웃
     *  POST
     *  SecurityConfig의 securityFilterChain() .logout() 설정으로 인해서 이 컨트롤러 메서드는 실행되지 않음
     * */
    @PostMapping("/logout")
    public String deleteUser(HttpServletResponse response) {
        Optional<Authentication> authState = userService.logOut();

        if (authState.isPresent()) {
            return "/error/errorPage";
        } else {
            return "redirect:/";
        }
    }


    /*
     * 회원 탈퇴
     * POST
     *
     * */
    @PostMapping("/quit")
    public ResponseEntity<?> deleteUser(@CookieValue("access_token") Cookie token,
                                        @RequestParam("type") String loginType) {
        try {
            if(loginType.equals("BASIC")) {
                userService.withdrawUser(token.getValue());
            } else {
                userService.withdrawKakaoUser(token.getValue());
            }

            return new ResponseEntity<>(HttpStatus.OK);

        } catch (Exception e) {
            log.error("회원 삭제 실패: {}", e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
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

    private static String bindErrorPage(BindingResult bindingResult, Model model) {
        model.addAttribute("errorMsg", bindingResult.getFieldError().getDefaultMessage());
        return "/error/errorPage";
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
