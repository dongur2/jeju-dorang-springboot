package com.donguri.jejudorang.domain.home.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Controller
@RequestMapping("/")
public class HomeController {
    @GetMapping
    public String home() {
        return "/home/home";
    }

    @PostMapping
    public String redirectHome() {
        return "/";
    }
}
