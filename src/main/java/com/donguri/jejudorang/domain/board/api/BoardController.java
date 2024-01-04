package com.donguri.jejudorang.domain.board.api;

import com.donguri.jejudorang.domain.board.dto.BoardWriteRequestDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Slf4j
@Controller
@RequestMapping("/board")
public class BoardController {

    @GetMapping("/list/{nowPage}")
    public String boardHome(@PathVariable("nowPage") Integer nowPage, Model model) {
        return "/board/boardList";
    }

    @GetMapping("/write")
    public String getBoardWriteForm() {
        return "/board/boardForm";
    }
    @PostMapping("/write")
    public String writeBoard(@ModelAttribute("board") BoardWriteRequestDto board, RedirectAttributes redirectAttributes) {
        log.info("form's data={}", board.toString());
        return "redirect:/board/list/0";
    }
}
