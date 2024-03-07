package com.donguri.jejudorang.domain.home.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Controller
@RequestMapping("/")
@Tag(name = "홈", description = "메인 화면 출력 API")
public class HomeController {

    @GetMapping
    @Operation(summary = "홈 화면 출력", description = "홈 화면을 반환합니다.")
    public String home() {
        return "home";
    }

    @PostMapping
    @Operation(summary = "홈 화면 리다이렉트", description = "홈 화면을 출력하도록 home()으로 리다이렉트합니다.")
    public String redirectHome() {
        return "redirect:/";
    }
}
