package com.donguri.jejudorang.domain.home.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Controller
@RequestMapping("/")
@Tag(name = "Home", description = "메인 홈 화면 리턴")
public class HomeController {

    @GetMapping
    @Operation(summary = "홈 화면", description = "홈 화면 출력")
    public String home() {
        return "home";
    }

    @PostMapping
    @Operation(summary = "홈 화면", description = "홈 화면 출력하도록 리다이렉트")
    public String redirectHome() {
        return "redirect:/";
    }
}
