package com.donguri.jejudorang.domain.board.api;

import com.donguri.jejudorang.domain.board.dto.request.BoardWriteRequestDto;
import com.donguri.jejudorang.domain.board.entity.Board;
import com.donguri.jejudorang.domain.board.service.BoardService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Slf4j
@Controller
@RequestMapping("/board")
public class BoardController {
    @Autowired
    private BoardService boardService;

    @GetMapping("/list/{nowPage}")
    public String boardHome(@PathVariable("nowPage") Integer nowPage, Model model) {
        return "/board/boardList";
    }

    @GetMapping("/write")
    public String getBoardWriteForm() {
        return "/board/boardForm";
    }
    @PostMapping("/write")
    public String writeBoard(BoardWriteRequestDto post, RedirectAttributes redirectAttributes, Model model) {
        log.info("form's data={}", post.toString());
        Board saved = boardService.savePost(post);
        log.info("saved data={}", saved.toString());
        return "redirect:/board/list/0";
    }
}
