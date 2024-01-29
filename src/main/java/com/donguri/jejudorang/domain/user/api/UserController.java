package com.donguri.jejudorang.domain.user.api;

import com.donguri.jejudorang.domain.user.dto.LoginRequest;
import com.donguri.jejudorang.domain.user.dto.ProfileResponse;
import com.donguri.jejudorang.domain.user.dto.SignUpRequest;
import com.donguri.jejudorang.domain.user.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
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


    @GetMapping("/signup")
    public String registerForm() {
        return "/user/login/signUpForm";
    }
    @PostMapping("/signup")
    public String registerUser(@Valid SignUpRequest signUpRequest, BindingResult bindingResult, Model model) {
        // 유효성 검사 에러
        if (bindingResult.hasErrors()) {
            return bindErrorPage(bindingResult, model);
        }

        try {
            userService.signUp(signUpRequest);
            return "redirect:/";

        } catch (Exception e) {
            model.addAttribute("errorMsg", e.getMessage());
            return "/error/errorTemp";
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




    private static String bindErrorPage(BindingResult bindingResult, Model model) {
        model.addAttribute("errorMsg", bindingResult.getFieldError().getDefaultMessage());
        return "/error/errorTemp";
    }
}
