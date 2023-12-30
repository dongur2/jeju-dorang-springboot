package com.donguri.jejudorang;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class test {
    @GetMapping
    public String testCon() {
        return "/main/main";
    }
}
