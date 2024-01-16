package com.donguri.jejudorang.domain.community.api;

import com.donguri.jejudorang.domain.community.dto.response.ChatDetailResponseDto;
import com.donguri.jejudorang.domain.community.service.ChatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static com.donguri.jejudorang.domain.community.api.CommunityController.convertToProperty;

@Slf4j
@Controller
@RequestMapping("/community/chats")
public class ChatController {
    @Autowired
    private ChatService chatsService;

    @Value("${kakao-api-key}")
    private String kakaoApiKey;


    /*
     * 잡담글 목록
     * /community/chats
     * GET
     * Query Parameters: page, order, (tag, search)
     *
     * */
    @GetMapping
    public String getChatList(@RequestParam(name = "page", required = false, defaultValue = "0") Integer nowPage,
                                           @RequestParam(name = "order", required = false, defaultValue = "recent") String order, // recent, comment, bookmark
                                           Model model) {

        // 넘어온 정렬 기준값 -> 컬럼명으로 변환
        order = convertToProperty(order);
        // 현재 페이지, 정렬 기준 컬럼명으로 Pageable 인스턴스
        Pageable pageable = PageRequest.of(nowPage, 5, Sort.by(order).descending());

        Map<String, Object> chatListInMap = chatsService.getChatPostList(pageable);

        // 뷰로 함께 리턴
        model.addAttribute("allChatPageCount", chatListInMap.get("allChatPageCount")); // 총 페이지 수
        model.addAttribute("chatListDtoPage", chatListInMap.get("chatListDtoPage")); // 데이터
        return "/community/communityChatList";
    }


    /*
    * 상세글 조회
    * /community/chats/{communityId}
    * GET
    *
    * */
    @GetMapping("/{communityId}")
    public String getChatDetail(@PathVariable("communityId") Long communityId, Model model) {
        ChatDetailResponseDto foundChatPost = chatsService.getChatPost(communityId);

        model.addAttribute("post", foundChatPost);
        model.addAttribute("kakaoApiKey", kakaoApiKey);
        return "/community/communityDetail";
    }
}
