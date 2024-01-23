package com.donguri.jejudorang.domain.user.api;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
public class UserController {
    @PostMapping("/login")
    public String login(HttpServletRequest request) {
        return null;
    }
}
