package com.donguri.jejudorang.domain.community.api;

import com.donguri.jejudorang.domain.community.dto.request.CommunityWriteRequestDto;
import com.donguri.jejudorang.domain.community.dto.response.CommunityForModifyResponseDto;
import com.donguri.jejudorang.domain.community.dto.response.CommunityTypeResponseDto;
import com.donguri.jejudorang.domain.community.service.ChatService;
import com.donguri.jejudorang.domain.community.service.CommunityService;
import com.donguri.jejudorang.domain.community.service.PartyService;
import jakarta.servlet.http.Cookie;
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

import java.util.HashMap;
import java.util.Map;


@Slf4j
@Controller
@RequestMapping("/community")
public class CommunityController {

    private final String kakaoApiKey;

    @Autowired private final CommunityService communityService;
    @Autowired private final ChatService chatService;
    @Autowired private final PartyService partyService;

    public CommunityController(@Value("${kakao-api-key}") String kakaoApiKey, CommunityService communityService, ChatService chatService, PartyService partyService) {
        this.kakaoApiKey = kakaoApiKey;
        this.communityService = communityService;
        this.chatService = chatService;
        this.partyService = partyService;
    }

    // default: api package 내에서만 사용 가능 - getPartyList, getCharList
    static String convertToProperty(String order) {
        if (order.equals("recent")) {
            order = "createdAt";
        } else if (order.equals("comment")) {
            order = "comments";
        } else if (order.equals("bookmark")) {
            order = "bookmarksCount";
        }
        return order;
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
        CommunityForModifyResponseDto foundPost = communityService.getCommunityPost(communityId);
        model.addAttribute("post", foundPost);
        return "/community/communityModifyForm";
    }

    @PutMapping("/post/{communityId}/modify")
    public String modifyCommunity(@PathVariable("communityId") Long communityId,
                                  @Valid CommunityWriteRequestDto postToUpdate,
                                  BindingResult bindingResult,
                                  Model model) {
        // 유효성 검사 에러
        if (bindingResult.hasErrors()) {
            return bindErrorPage(bindingResult, model);
        }

        try {
            CommunityTypeResponseDto redirectTypeDto = communityService.updatePost(communityId, postToUpdate);
            return "redirect:/community/" + redirectTypeDto.typeForRedirect() + "/{communityId}";

        } catch (Exception e) {
            model.addAttribute("errorMsg", e.getMessage());
            return "/error/errorTemp";
        }
    }

    private static String bindErrorPage(BindingResult bindingResult, Model model) {
        model.addAttribute("errorMsg", bindingResult.getFieldError().getDefaultMessage());
        return "/error/errorTemp";
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
    * */
    @GetMapping("/boards/{type}")
    public String getCommunityList(@PathVariable(name = "type") String type,
                                   @RequestParam(name = "page", required = false, defaultValue = "0") Integer nowPage,
                                   @RequestParam(name = "state", required = false, defaultValue = "all") String state, // all, recruiting, done
                                   @RequestParam(name = "order", required = false, defaultValue = "recent") String order, // recent, comment, bookmark
                                   @RequestParam(name = "search", required = false) String searchWord,
                                   @RequestParam(name = "tags", required = false) String searchTag,
                                   Model model) {

        // 넘어온 정렬 기준값 -> 컬럼명으로 변환
        order = convertToProperty(order);
        // 현재 페이지, 정렬 기준 컬럼명으로 Pageable 인스턴스
        Pageable pageable = PageRequest.of(nowPage, 5, Sort.by(order).descending());

        Map<String, Object> listInMap;
        if(type.equals("parties")) {
            listInMap = partyService.getPartyPostList(pageable, state, searchWord, searchTag);
        } else {
            listInMap = chatService.getChatPostList(pageable, searchWord, searchTag);
        }

        model.addAttribute("nowType", type);

        model.addAttribute("order", order);
        model.addAttribute("nowPage", nowPage);

        model.addAttribute("currentSearchWord", searchWord);
        model.addAttribute("currentSearchTag", searchTag);

        if(type.equals("parties")) {
            model.addAttribute("nowState", state);
            model.addAttribute("allPartyPageCount", listInMap.get("allPartyPageCount")); // 총 페이지 수
            model.addAttribute("partyListDtoPage", listInMap.get("partyListDtoPage")); // 데이터
        } else {
            model.addAttribute("allChatPageCount", listInMap.get("allChatPageCount")); // 총 페이지 수
            model.addAttribute("chatListDtoPage", listInMap.get("chatListDtoPage")); // 데이터
        }

        return "/community/communityList";
    }

}
