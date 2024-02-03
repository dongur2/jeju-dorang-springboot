package com.donguri.jejudorang.domain.community.api;

import com.donguri.jejudorang.domain.community.dto.response.ChatDetailResponseDto;
import com.donguri.jejudorang.domain.community.service.ChatService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

import static com.donguri.jejudorang.domain.community.api.CommunityController.convertToProperty;

@Slf4j
@Controller
@RequestMapping("/community/chats")
public class ChatController {
    @Autowired
    private ChatService chatsService;

    @Value("${kakao-api-key}")
    private String kakaoApiKey;

    @Value("${view.cookie-expire}")
    private int viewCookieTime;


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
                              @RequestParam(name = "search", required = false) String searchWord,
                              @RequestParam(name = "tags", required = false) String searchTag,
                              Model model) {

        // 넘어온 정렬 기준값 -> 컬럼명으로 변환
        order = convertToProperty(order);
        // 현재 페이지, 정렬 기준 컬럼명으로 Pageable 인스턴스
        Pageable pageable = PageRequest.of(nowPage, 5, Sort.by(order).descending());

        Map<String, Object> chatListInMap = chatsService.getChatPostList(pageable, searchWord, searchTag);


        model.addAttribute("currentSearchWord", searchWord);
        model.addAttribute("currentSearchTag", searchTag);

        model.addAttribute("nowPage", nowPage);
        model.addAttribute("order", order);

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
    public String getChatDetail(@PathVariable("communityId") Long communityId,
                                HttpServletRequest request, HttpServletResponse response,
                                Model model) {

        // 쿠키 체크 & 조회수 업데이트 여부 결정 & 조건 충족할 경우 조회수, 쿠키 업데이트
        checkIsAlreadyReadForUpdateView(communityId, request, response);

        ChatDetailResponseDto foundChatPost = chatsService.getChatPost(communityId);

        model.addAttribute("post", foundChatPost);
        model.addAttribute("kakaoApiKey", kakaoApiKey);
        return "/community/communityDetail";
    }

    private void checkIsAlreadyReadForUpdateView(Long communityId, HttpServletRequest request, HttpServletResponse response) {

        Optional<Cookie[]> cookies = Optional.ofNullable(request.getCookies());

        // 쿠키가 아예 없거나(비회원) 상세글 조회 목록 쿠키가 없는 경우(회원: 액세스 토큰 쿠키 존재)
        if (cookies.isEmpty() || Arrays.stream(cookies.get()).filter(cookie -> cookie.getName().equals("isRead")).toList().isEmpty()) {

            Cookie newCookie = new Cookie("isRead", String.valueOf(communityId));
            updateCookie(response, newCookie);
            log.info("새로운 쿠키 생성  {} : {}", newCookie.getName(), newCookie.getValue());

            chatsService.updateChatView(communityId);
            log.info("조회수 증가 완료");


            // 상세글 조회 목록 쿠키가 있는 경우
        } else {
            Cookie isReadCookie = Arrays.stream(cookies.get())
                    .filter(coo -> coo.getName().equals("isRead"))
                    .toList().get(0);

            boolean communityIdExists = Arrays.asList(isReadCookie.getValue().split("/")).contains(String.valueOf(communityId));

            // 현재 communityId가 쿠키 값에 포함되어 있지 않은 경우
            if (!communityIdExists) {
                StringBuilder newValueBuilder = new StringBuilder();
                newValueBuilder.append(isReadCookie.getValue()).append("/").append(communityId);

                isReadCookie.setValue(newValueBuilder.toString());
                updateCookie(response, isReadCookie);
                log.info("쿠키에 새로운 communityId 추가: {} -> {}", communityId, newValueBuilder);

                chatsService.updateChatView(communityId);
                log.info("조회수 증가 완료");

                // 현재 communityId가 쿠키 값에 포함된 경우
            } else {
                log.info("이미 조회한 글입니다.");
            }
        }
    }

    private void updateCookie(HttpServletResponse response, Cookie newCookie) {
        newCookie.setHttpOnly(true);
        newCookie.setMaxAge(viewCookieTime);
        newCookie.setPath("/");
        response.addCookie(newCookie);
    }

}


