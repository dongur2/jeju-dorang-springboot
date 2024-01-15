package com.donguri.jejudorang.domain.community.api;

import com.donguri.jejudorang.domain.community.dto.request.CommunityUpdateRequestDto;
import com.donguri.jejudorang.domain.community.dto.request.CommunityWriteRequestDto;
import com.donguri.jejudorang.domain.community.dto.response.CommunityDetailResponseDto;
import com.donguri.jejudorang.domain.community.dto.response.PartyListResponseDto;
import com.donguri.jejudorang.domain.community.entity.Community;
import com.donguri.jejudorang.domain.community.service.CommunityService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/community")
public class CommunityController {
    @Autowired
    private CommunityService communityService;

    @Value("${kakao-api-key}")
    private String kakaoApiKey;

    @GetMapping("/parties")
    @ResponseBody
    public Map<String, Object> getPartyList(@RequestParam(name = "page", required = false, defaultValue = "0") Integer nowPage,
                                            @RequestParam(name = "state", required = false) String state, // recruiting, done
                                            @RequestParam(name = "order", required = false, defaultValue = "recent") String order, // recent, comment, liked
                                            Model model) {

        log.info("page={}, state={}, order={}",nowPage,state,order);

        // 넘어온 정렬 기준값 -> 컬럼명으로 변환
        order = convertToProperty(order);
        // 현재 페이지, 정렬 기준 컬럼명으로 Pageable 인스턴스
        Pageable pageable = PageRequest.of(nowPage, 5, Sort.by(order).descending());

        Map<String, Object> partyListInMap = communityService.getPartyPostList(pageable, state);

        // 뷰로 함께 리턴
        model.addAttribute("allPartyPageCount", partyListInMap.get("allPartyPageCount")); // 총 페이지 수
        model.addAttribute("partyListDtoPage", partyListInMap.get("partyListDtoPage")); // 데이터
        return partyListInMap;
    }

    @GetMapping("/chats")
    @ResponseBody
    public Map<String, Object> getChatList(@RequestParam(name = "page", required = false, defaultValue = "0") Integer nowPage,
                                           @RequestParam(name = "order", required = false, defaultValue = "recent") String order, // recent, comment, liked
                                           Model model) {

        log.info("page={}, order={}",nowPage,order);

        // 넘어온 정렬 기준값 -> 컬럼명으로 변환
        order = convertToProperty(order);
        // 현재 페이지, 정렬 기준 컬럼명으로 Pageable 인스턴스
        Pageable pageable = PageRequest.of(nowPage, 5, Sort.by(order).descending());

        Map<String, Object> chatListInMap = communityService.getChatPostList(pageable);

        // 뷰로 함께 리턴
        model.addAttribute("allChatPageCount", chatListInMap.get("allChatPageCount")); // 총 페이지 수
        model.addAttribute("chatListDtoPage", chatListInMap.get("chatListDtoPage")); // 데이터
        return chatListInMap;
    }

    private static String convertToProperty(String order) {
        if (order.equals("recent")) {
            order = "createdAt";
        } else if (order.equals("comment")) {
            order = "comments";
        } else if (order.equals("liked")) {
            order = "liked";
        }
        return order;
    }

    @GetMapping("/write")
    public String getBoardWriteForm() {
        return "/board/boardForm";
    }

    @PostMapping("/write")
    public String writeBoard(CommunityWriteRequestDto post, RedirectAttributes redirectAttributes, Model model) {
        communityService.savePost(post);

        String boardType = post.getType().toLowerCase();
        log.info("boardType={}", boardType);
        return "redirect:/board/" + boardType + "/list/createdAt/0";
    }

    @GetMapping("/detail/{boardId}")
    public String boardDetail(@PathVariable("boardId") Long boardId, Model model) {
        CommunityDetailResponseDto foundPost = communityService.getPost(boardId);

        model.addAttribute("post", foundPost);
        model.addAttribute("kakaoApiKey", kakaoApiKey);
        return "/board/boardDetail";
    }

    @GetMapping("/detail/{boardId}/modify")
    public String getBoardModifyForm(@PathVariable("boardId") Long boardId, Model model) {
        CommunityDetailResponseDto foundPost = communityService.getPost(boardId);
        model.addAttribute("post", foundPost);
        return "/board/boardModifyForm";
    }

    @PutMapping("/detail/{boardId}/modify")
    public String modifyBoard(@PathVariable("boardId") Long boardId, CommunityUpdateRequestDto post) {
        communityService.updatePost(boardId, post);
        return "redirect:/board/detail/{boardId}";
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/detail/{boardId}/modifyJoining")
    public void modifyBoardJoinState(@PathVariable("boardId") Long boardId) {
        communityService.changePartyJoinState(boardId);
    }

}
