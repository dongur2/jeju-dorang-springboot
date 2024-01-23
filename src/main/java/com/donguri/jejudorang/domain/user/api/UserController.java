package com.donguri.jejudorang.domain.user.api;

import com.donguri.jejudorang.global.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
public class UserController {
    @PostMapping("/login")
    public String login(HttpServletRequest request) {
        log.info(request.getHeader("Authorization"));
        JwtUtil jwtUtil = new JwtUtil();
        return jwtUtil.generateToken(request.getHeader("Authorization"));
    }

    @GetMapping("/protected")
    @Secured("USER") // Role-based authorization
    public String protectedResource() {
        return "This is a protected resource.";
    }
}
