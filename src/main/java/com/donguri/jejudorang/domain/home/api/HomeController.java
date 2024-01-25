package com.donguri.jejudorang.domain.home.api;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequestMapping("/")
public class HomeController {
    @GetMapping
    public String home(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("access_token")) {
                log.info(cookie.getValue());
            }
        }

        return "/home/home";
    }
}
