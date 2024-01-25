package com.donguri.jejudorang.domain.user.api;

import com.donguri.jejudorang.domain.user.dto.JwtAuthResponse;
import com.donguri.jejudorang.domain.user.dto.LoginRequest;
import com.donguri.jejudorang.domain.user.dto.SignUpRequest;
import com.donguri.jejudorang.domain.user.service.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;



@Slf4j
@Controller
@RequestMapping("/user")
public class UserController {

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
            return "/home/home";

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
    public ResponseEntity<?> authenticateUser(@Valid LoginRequest loginRequest, BindingResult bindingResult, Model model) {
        return userService.signIn(loginRequest);
    }


    private static String bindErrorPage(BindingResult bindingResult, Model model) {
        model.addAttribute("errorMsg", bindingResult.getFieldError().getDefaultMessage());
        return "/error/errorTemp";
    }
}
