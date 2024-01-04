package com.donguri.jejudorang.domain.board.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequestMapping("/board")
public class BoardController {

    @GetMapping("/list/{nowPage}")
    public String boardHome(@PathVariable("nowPage") Integer nowPage, Model model) {
        return "/board/boardList";
    }

    @GetMapping("/write")
    public String getWriteForm() {
        return "/board/boardForm";
    }
}
