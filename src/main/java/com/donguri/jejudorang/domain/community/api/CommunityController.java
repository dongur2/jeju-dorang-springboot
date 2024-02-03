package com.donguri.jejudorang.domain.community.api;

import com.donguri.jejudorang.domain.community.dto.request.CommunityWriteRequestDto;
import com.donguri.jejudorang.domain.community.dto.response.*;
import com.donguri.jejudorang.domain.community.service.ChatService;
import com.donguri.jejudorang.domain.community.service.CommunityService;
import com.donguri.jejudorang.domain.community.service.PartyService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;


@Slf4j
@Controller
@RequestMapping("/community")
public class CommunityController {

    private final String kakaoApiKey;
    private final int viewCookieTime;

    @Autowired private final CommunityService communityService;
    @Autowired private final ChatService chatService;
    @Autowired private final PartyService partyService;

    public CommunityController(@Value("${kakao-api-key}") String kakaoApiKey,
                               @Value("${view.cookie-expire}") int viewCookieTime,
                               CommunityService communityService, ChatService chatService, PartyService partyService) {
        this.kakaoApiKey = kakaoApiKey;
        this.viewCookieTime = viewCookieTime;
        this.communityService = communityService;
        this.chatService = chatService;
        this.partyService = partyService;
    }


    /*
    * 글 작성
    * /community/post/new
    * GET, POST
    *
    * */
    @GetMapping("/post/new")
    public String getCommunityWriteForm(@RequestParam(name = "type") String preType, Model model) {
        model.addAttribute("type", preType); // 미리 설정되는 글타입
        return "/community/communityPostForm";
    }

    @PostMapping("/post/new")
    public ResponseEntity<?> postNewCommunity(@Valid CommunityWriteRequestDto postToWrite, BindingResult bindingResult,
                                              @CookieValue("access_token") Cookie token) {

        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>(bindingResult.getFieldError().getDefaultMessage(), HttpStatus.BAD_REQUEST);
        }

        try {
            CommunityTypeResponseDto communityTypeResponseDto = communityService.saveNewPost(postToWrite, token.getValue());
            return new ResponseEntity<>(communityTypeResponseDto.typeForRedirect(), HttpStatus.OK);

        } catch (Exception e) {
            log.error("게시글 작성에 실패했습니다 : {}", e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }


    /*
    * 글 수정
    * /community/post/{communityId}/modify
    * GET, PUT
    *
    * */
    @GetMapping("/post/{communityId}/modify")
    public String getCommunityModifyForm(@PathVariable("communityId") Long communityId, Model model) {
        try {
            CommunityForModifyResponseDto foundPost =
                    (CommunityForModifyResponseDto) communityService.getCommunityPost(communityId, true).get("result");

            model.addAttribute("post", foundPost);
            return "/community/communityModifyForm";

        } catch (Exception e) {
            log.error("수정 데이터 불러오기 실패: {}", e.getMessage());
            model.addAttribute("errorMsg", e.getMessage());
            return "/error/errorTemp";
        }
    }

    @PutMapping("/post/{communityId}/modify")
    public String modifyCommunity(@PathVariable("communityId") Long communityId,
                                  @Valid CommunityWriteRequestDto postToUpdate,
                                  BindingResult bindingResult,
                                  Model model) {

        try {
            if (bindingResult.hasErrors()) {
                throw new IllegalArgumentException(bindingResult.getFieldError().getDefaultMessage());
            }

            CommunityTypeResponseDto redirectTypeDto = communityService.updatePost(communityId, postToUpdate);
            return "redirect:/community/" + redirectTypeDto.typeForRedirect() + "/{communityId}";

        } catch (Exception e) {
            model.addAttribute("errorMsg", e.getMessage());
            return "/error/errorTemp";
        }
    }


    /*
    * 게시글 메인 - 목록 불러오기
    * /community/boards/{type} // parties, chats
    * GET
    *
    * > Parameters
    * @PathVariable
    * String type: 글 목록 타입
    *
    * @RequestParam
    * Integer page: 현재 페이지
    * String state: 정렬 기준 모집 상태 - party
    * String order: 정렬 기준
    * String search: 검색어
    * String searchTag: 검색 태그 (A,B,C,...,N개)
    *
    * > Return Model Attributes
    * String nowType: 글 목록 타입 - parties, chats
    * String nowState: 목록 정렬한 모집 상태 - all / recruiting / done - party
    * String order: 정렬 기준 - recent, comment, bookmark
    * int nowPage: 지금 페이지
    * String currentSearchWord: 검색어
    * String currentSearchTag: 검색 태그
    *
    * int allPartyPageCount/allChatPageCount: 목록 전체 페이지 수
    * Page<Community> partyListDtoPage/chatListDtoPage: 글 데이터
    * */
    @GetMapping("/boards/{type}")
    public String getCommunityList(@PathVariable(name = "type") String type,
                                   @RequestParam(name = "page", required = false, defaultValue = "0") Integer nowPage,
                                   @RequestParam(name = "state", required = false, defaultValue = "all") String state, // all, recruiting, done
                                   @RequestParam(name = "order", required = false, defaultValue = "recent") String order, // recent, comment, bookmark
                                   @RequestParam(name = "search", required = false) String searchWord,
                                   @RequestParam(name = "tags", required = false) String searchTag,
                                   Model model) {

        try {
            // 넘어온 정렬 기준값 -> 컬럼명으로 변환
            order = convertToProperty(order);
            // 현재 페이지, 정렬 기준 컬럼명으로 Pageable 인스턴스
            Pageable pageable = PageRequest.of(nowPage, 5, Sort.by(order).descending());

            Map<String, Object> listInMap;
            if (type.equals("parties")) {
                listInMap = partyService.getPartyPostList(pageable, state, searchWord, searchTag);
            } else {
                listInMap = chatService.getChatPostList(pageable, searchWord, searchTag);
            }

            model.addAttribute("nowType", type);

            model.addAttribute("order", order);
            model.addAttribute("nowPage", nowPage);

            model.addAttribute("currentSearchWord", searchWord);
            model.addAttribute("currentSearchTag", searchTag);

            if (type.equals("parties")) {
                model.addAttribute("nowState", state);
                model.addAttribute("allPartyPageCount", listInMap.get("allPartyPageCount")); // 총 페이지 수
                model.addAttribute("partyListDtoPage", listInMap.get("partyListDtoPage")); // 데이터
            } else {
                model.addAttribute("allChatPageCount", listInMap.get("allChatPageCount")); // 총 페이지 수
                model.addAttribute("chatListDtoPage", listInMap.get("chatListDtoPage")); // 데이터
            }

            return "/community/communityList";

        } catch (Exception e) {
            log.error("게시글 목록 불러오기 실패: {}", e.getMessage());
            model.addAttribute("errorMsg", e.getMessage());
            return "/error/errorTemp";
        }
    }

    /*
     * 넘어온 정렬 기준 -> 실제 DB 컬럼명으로 변환
     * */
    private String convertToProperty(String order) {
        switch (order) {
            case "recent" -> order = "createdAt";
            case "comment" -> order = "comments";
            case "bookmark" -> order = "bookmarksCount";
        }
        return order;
    }


    /*
     * 상세글 조회
     * /community/{type}/{communityId}
     * GET
     *
     * {type} = parties, chats
     *
     * */
    @GetMapping("/boards/{type}/{communityId}")
    public String getCommunityDetail(@PathVariable("communityId") Long communityId,
                                     HttpServletRequest request, HttpServletResponse response,
                                     Model model) {

        try {
            // 쿠키 체크 & 조회수 업데이트 여부 결정 & 조건 충족할 경우 조회수, 쿠키 업데이트
            checkIsAlreadyReadForUpdateView(communityId, request, response);

            CommunityDetailResponseDto foundPPost =
                    (CommunityDetailResponseDto) communityService.getCommunityPost(communityId, false).get("result");

            model.addAttribute("post", foundPPost);
            model.addAttribute("kakaoApiKey", kakaoApiKey);
            return "/community/communityDetail";

        } catch (Exception e) {
            log.error("상세글 불러오기 실패: {}", e.getMessage());
            model.addAttribute("errorMsg", e.getMessage());
            return "/error/errorTemp";
        }
    }

    /*
     * 조회수 중복 필터링 (쿠키 확인)
     *
     * # 조회수 증가
     * 1. 비회원: 쿠키가 아예 없는 경우  [ isRead 쿠키 생성 ]
     * 2. 회원: 글 조회 목록 쿠키(isRead)가 없는 경우   [ isRead 쿠키 생성 ]
     * 3. 회원: 글 조회 목록 쿠키(isRead)는 존재하지만, 현재 글 아이디가 포함되지 않은 경우   [ isRead 쿠키 업데이트 ]
     *
     * # 조회수, 쿠키 고정
     * 글 조회 목록 쿠키(isRead)에 현재 글 아이디가 포함될 경우
     *
     * */
    private void checkIsAlreadyReadForUpdateView(Long communityId, HttpServletRequest request, HttpServletResponse response) {

        Optional<Cookie[]> cookies = Optional.ofNullable(request.getCookies());

        // 쿠키가 아예 없거나(비회원) 상세글 조회 목록 쿠키가 없는 경우(회원: 액세스 토큰 쿠키 존재)
        if (cookies.isEmpty() || Arrays.stream(cookies.get()).filter(cookie -> cookie.getName().equals("isRead")).toList().isEmpty()) {

            Cookie newCookie = new Cookie("isRead", String.valueOf(communityId));
            updateCookie(response, newCookie);
            log.info("새로운 쿠키 생성  {} : {}", newCookie.getName(), newCookie.getValue());

            communityService.updateView(communityId);
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

                communityService.updateView(communityId);
                log.info("조회수 증가 완료");

                // 현재 communityId가 쿠키 값에 포함된 경우
            } else {
                log.info("이미 조회한 글입니다.");
            }
        }
    }

    // 쿠키 값 설정 후 Response에 추가
    private void updateCookie(HttpServletResponse response, Cookie newCookie) {
        newCookie.setHttpOnly(true);
        newCookie.setMaxAge(viewCookieTime);
        newCookie.setPath("/");
        response.addCookie(newCookie);
    }

}
