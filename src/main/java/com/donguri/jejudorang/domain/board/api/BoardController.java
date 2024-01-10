package com.donguri.jejudorang.domain.board.api;

import com.donguri.jejudorang.domain.board.dto.request.BoardUpdateRequestDto;
import com.donguri.jejudorang.domain.board.dto.request.BoardWriteRequestDto;
import com.donguri.jejudorang.domain.board.entity.Board;
import com.donguri.jejudorang.domain.board.service.BoardService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("/board")
public class BoardController {
    @Autowired
    private BoardService boardService;

    @GetMapping("/list/{nowPage}")
    public String boardHome(@PathVariable("nowPage") Integer nowPage, Model model) {
        List<Board> allPosts = boardService.getAllPosts();
        model.addAttribute("posts", allPosts);
        return "/board/boardList";
    }

    @GetMapping("/write")
    public String getBoardWriteForm() {
        return "/board/boardForm";
    }

    @PostMapping("/write")
    public String writeBoard(BoardWriteRequestDto post, RedirectAttributes redirectAttributes, Model model) {
        Board saved = boardService.savePost(post);
        log.info("saved data={}", saved.toString());
        return "redirect:/board/list/0";
    }

    @GetMapping("/detail/{boardId}")
    public String boardDetail(@PathVariable("boardId") Long boardId, Model model) {
        // 조회수 +1 * 나중에 작성자 아이디랑 현재 로그인 아이디 불일치인 상황에서만 추가
        Board foundPost = boardService.getPost(boardId);
        model.addAttribute("post", foundPost);
        return "/board/boardDetail";
    }

    @GetMapping("/detail/{boardId}/modify")
    public String getBoardModifyForm(@PathVariable("boardId") Long boardId, Model model) {
        Board foundPost = boardService.getPost(boardId);
        model.addAttribute("post", foundPost);
        return "/board/boardModifyForm";
    }

    @PutMapping("/detail/{boardId}/modify")
    public String modifyBoard(@PathVariable("boardId") Long boardId, BoardUpdateRequestDto post) {
        boardService.updatePost(boardId, post);
        log.info("BoardUpdateRequestDto={}", post);
        return "redirect:/board/detail/{boardId}";
    }
}
