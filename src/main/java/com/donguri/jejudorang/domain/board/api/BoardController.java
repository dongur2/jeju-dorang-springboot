package com.donguri.jejudorang.domain.board.api;

import com.donguri.jejudorang.domain.board.dto.request.BoardUpdateRequestDto;
import com.donguri.jejudorang.domain.board.dto.request.BoardWriteRequestDto;
import com.donguri.jejudorang.domain.board.entity.Board;
import com.donguri.jejudorang.domain.board.service.BoardService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${kakao-api-key}")
    private String kakaoApiKey;

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
        return "redirect:/board/detail/" + saved.getId();
    }

    @GetMapping("/detail/{boardId}")
    public String boardDetail(@PathVariable("boardId") Long boardId, Model model) {
        Board foundPost = boardService.getPost(boardId);
        model.addAttribute("post", foundPost);
        model.addAttribute("kakaoApiKey", kakaoApiKey);
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
        return "redirect:/board/detail/{boardId}";
    }

    @ResponseBody
    @PutMapping("/detail/{boardId}/modifyJoining")
    public void modifyBoardJoinState(@PathVariable("boardId") Long boardId) {
        boardService.changePartyJoinState(boardId);
    }
}
