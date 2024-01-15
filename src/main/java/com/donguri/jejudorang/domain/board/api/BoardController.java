package com.donguri.jejudorang.domain.board.api;

import com.donguri.jejudorang.domain.board.dto.request.BoardUpdateRequestDto;
import com.donguri.jejudorang.domain.board.dto.request.BoardWriteRequestDto;
import com.donguri.jejudorang.domain.board.dto.response.BoardDetailResponseDto;
import com.donguri.jejudorang.domain.board.dto.response.BoardListResponseDto;
import com.donguri.jejudorang.domain.board.entity.Board;
import com.donguri.jejudorang.domain.board.service.BoardService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("/board")
public class BoardController {
    @Autowired
    private BoardService boardService;

    @Value("${kakao-api-key}")
    private String kakaoApiKey;

    @GetMapping("/{boardType}/list/{criteria}/{nowPage}")
    public String boardHome(@PathVariable("boardType") String boardType,
                            @PathVariable("criteria") String criteria,
                            @PathVariable("nowPage") Integer nowPage,
                            Model model) {
        log.info("SORTED BY CRITERIA = {}", criteria);
        Pageable pageable = PageRequest.of(nowPage, 5, Sort.by(criteria).descending());
        Map<String, Object> allPostsInMap = boardService.getAllPosts(pageable, boardType);

        model.addAttribute("nowPostType", boardType); // 모임 or 잡담 구분
        model.addAttribute("nowPostSortCriteria", criteria); // 정렬 기준
        model.addAttribute("postAllPageCount", allPostsInMap.get("boardCounts")); // 총 페이지 수
        model.addAttribute("posts", allPostsInMap.get("boardPage")); // 페이지
        return "/board/boardList";
    }

    @GetMapping("/write")
    public String getBoardWriteForm() {
        return "/board/boardForm";
    }

    @PostMapping("/write")
    public String writeBoard(BoardWriteRequestDto post, RedirectAttributes redirectAttributes, Model model) {
        boardService.savePost(post);

        String boardType = post.getType().toLowerCase();
        log.info("boardType={}", boardType);
        return "redirect:/board/" + boardType + "/list/createdAt/0";
    }

    @GetMapping("/detail/{boardId}")
    public String boardDetail(@PathVariable("boardId") Long boardId, Model model) {
        BoardDetailResponseDto foundPost = boardService.getPost(boardId);

        model.addAttribute("post", foundPost);
        model.addAttribute("kakaoApiKey", kakaoApiKey);
        return "/board/boardDetail";
    }

    @GetMapping("/detail/{boardId}/modify")
    public String getBoardModifyForm(@PathVariable("boardId") Long boardId, Model model) {
        BoardDetailResponseDto foundPost = boardService.getPost(boardId);
        model.addAttribute("post", foundPost);
        return "/board/boardModifyForm";
    }

    @PutMapping("/detail/{boardId}/modify")
    public String modifyBoard(@PathVariable("boardId") Long boardId, BoardUpdateRequestDto post) {
        boardService.updatePost(boardId, post);
        return "redirect:/board/detail/{boardId}";
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/detail/{boardId}/modifyJoining")
    public void modifyBoardJoinState(@PathVariable("boardId") Long boardId) {
        boardService.changePartyJoinState(boardId);
    }

}
