package com.donguri.jejudorang.domain.community.api;

import com.donguri.jejudorang.domain.community.dto.request.CommunityUpdateRequestDto;
import com.donguri.jejudorang.domain.community.dto.request.CommunityWriteRequestDto;
import com.donguri.jejudorang.domain.community.dto.response.CommunityDetailResponseDto;
import com.donguri.jejudorang.domain.community.dto.response.CommunityTypeResponseDto;
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

import java.util.Map;

@Slf4j
@Controller
@RequestMapping("/community")
public class CommunityController {
    @Autowired
    private CommunityService communityService;

    @Value("${kakao-api-key}")
    private String kakaoApiKey;


    // default: api package 내에서만 사용 가능 - getPartyList, getCharList
    static String convertToProperty(String order) {
        if (order.equals("recent")) {
            order = "createdAt";
        } else if (order.equals("comment")) {
            order = "comments";
        } else if (order.equals("liked")) {
            order = "liked";
        }
        return order;
    }

    @GetMapping("/post/new")
    public String getCommunityWriteForm(@RequestParam(name = "type") String type, Model model) {
        model.addAttribute("type", type); // 미리 설정되는 글타입
        return "/community/communityPostForm";
    }

    @PostMapping("/post/new")
    public String postNewCommunity(CommunityWriteRequestDto post, Model model) {
        CommunityTypeResponseDto communityTypeResponseDto = communityService.saveNewPost(post);

        return "redirect:/community/" + communityTypeResponseDto.getTypeForRedirect();
    }



    @GetMapping("/{type}/{boardId}")
    public String boardDetail(@PathVariable("boardId") Long boardId, Model model) {
        CommunityDetailResponseDto foundPost = communityService.getPost(boardId);

        model.addAttribute("post", foundPost);
        model.addAttribute("kakaoApiKey", kakaoApiKey);
        return "/community/communityDetail";
    }



    @GetMapping("/detail/{boardId}/modify")
    public String getBoardModifyForm(@PathVariable("boardId") Long boardId, Model model) {
        CommunityDetailResponseDto foundPost = communityService.getPost(boardId);
        model.addAttribute("post", foundPost);
        return "/community/communityModifyForm";
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
