package com.donguri.jejudorang.domain.user.api;

import com.donguri.jejudorang.domain.user.dto.LoginRequest;
import com.donguri.jejudorang.domain.user.dto.SignUpRequest;
import com.donguri.jejudorang.domain.user.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;


@Slf4j
@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired private final UserService userService;

    @Value("${jwt.header}")
    private String jwtHeader;

    @Value("${jwt.header.prefix}")
    private String jwtPrefix;

    @Value("${jwt.cookie-expire}")
    private int cookieTime;

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
                                   HttpServletResponse response) {

        String jwtAccess = userService.signIn(loginRequest);

        if (jwtAccess == null) {
            return "redirect:/user/login";

        } else {

            Cookie resCookie = new Cookie("access_token", jwtAccess);
            resCookie.setHttpOnly(true);
            resCookie.setMaxAge(cookieTime);
            resCookie.setPath("/");

            response.addCookie(resCookie);

            return "redirect:/";
        }

    }

    @GetMapping("/logout")
    public String deleteCookie(@CookieValue(value = "access_token", defaultValue = "", required = false) Cookie cookie,
                               HttpServletResponse response) {
        log.info("LOGOUT ========= !!");

        Optional<Authentication> authState = userService.logOut();
        if (authState.isPresent()) {
            log.error("로그아웃 실패");
            return "/error/errorTemp";
        } else {
            log.info("로그아웃 성공");

            cookie.setHttpOnly(true);
            cookie.setValue(null);
            cookie.setMaxAge(0);
            response.addCookie(cookie);

            return "redirect:/";
        }

    }

    private static String bindErrorPage(BindingResult bindingResult, Model model) {
        model.addAttribute("errorMsg", bindingResult.getFieldError().getDefaultMessage());
        return "/error/errorTemp";
    }
}
