package com.donguri.jejudorang.domain.community.api;

import com.donguri.jejudorang.domain.community.api.swagger.CommunityControllerDocs;
import com.donguri.jejudorang.domain.community.dto.request.CommunityWriteRequest;
import com.donguri.jejudorang.domain.community.dto.response.*;
import com.donguri.jejudorang.domain.community.service.ChatService;
import com.donguri.jejudorang.domain.community.service.CommunityService;
import com.donguri.jejudorang.domain.community.service.PartyService;
import com.donguri.jejudorang.global.error.CustomException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
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
public class CommunityController implements CommunityControllerDocs {
    private final String kakaoApiKey;
    private final int viewCookieTime;

    private final ChatService chatService;
    private final PartyService partyService;
    private final CommunityService communityService;

    @Autowired
    public CommunityController(@Value("${kakao.key}") String kakaoApiKey,
                               @Value("${view.cookie-expire}") int viewCookieTime,
                               ChatService chatService, PartyService partyService, CommunityService communityService) {
        this.kakaoApiKey = kakaoApiKey;
        this.viewCookieTime = viewCookieTime;
        this.chatService = chatService;
        this.partyService = partyService;
        this.communityService = communityService;
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
        return "community/communityPostForm";
    }

    @PostMapping("/post/new")
    public ResponseEntity<?> postNewCommunity(@Valid CommunityWriteRequest postToWrite, BindingResult bindingResult,
                                              @CookieValue("access_token") Cookie token) {

        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>(bindingResult.getFieldError().getDefaultMessage(), HttpStatus.BAD_REQUEST);
        }

        try {
            CommunityTypeResponse communityTypeResponseDto = communityService.saveNewPost(postToWrite, token.getValue());
            return new ResponseEntity<>(communityTypeResponseDto.typeForRedirect(), HttpStatus.OK);

        } catch (CustomException e) {
            log.error("게시글 작성에 실패했습니다 : {}", e.getCustomErrorCode().getMessage());
            return new ResponseEntity<>(e.getCustomErrorCode().getMessage(), e.getCustomErrorCode().getStatus());

        } catch (Exception e) {
            log.error("게시글 작성에 실패했습니다 : [서버 오류] {}", e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    /*
    * 글 수정
    * /community/post/{communityId}/modify
    * GET, PUT
    *
    * */
    @GetMapping("/post/{communityId}/modify")
    public String getCommunityModifyForm(@PathVariable("communityId") Long communityId, Model model, HttpServletResponse response) {
        try {
            model.addAttribute("post", communityService.getCommunityPost(communityId, true, null).get("result"));
            return "community/communityModifyForm";

        } catch (CustomException e) {
            response.setStatus(404);
            model.addAttribute("message", e.getCustomErrorCode().getMessage());
            return "404";
        }
    }

    @PutMapping("/post/{communityId}/modify")
    public ResponseEntity<?> modifyCommunity(@PathVariable("communityId") Long communityId,
                                              @Valid CommunityWriteRequest postToUpdate,
                                              BindingResult bindingResult) {
        try {
            if (bindingResult.hasErrors()) {
                return new ResponseEntity<>(bindingResult.getFieldError().getDefaultMessage(), HttpStatus.BAD_REQUEST);
            }

            CommunityTypeResponse redirectTypeDto = communityService.updatePost(communityId, postToUpdate);
            return new ResponseEntity<>("/community/boards/" + redirectTypeDto.typeForRedirect() + "/" + communityId, HttpStatus.OK);


        } catch (CustomException e) {
            log.error("글 수정 실패: {}", e.getCustomErrorCode().getMessage());
            return new ResponseEntity<>(e.getCustomErrorCode().getMessage(), e.getCustomErrorCode().getStatus());

        } catch (Exception e) {
            log.error("글 수정 실패: [서버 오류] {}", e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
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
                                   @RequestParam(name = "page", required = false, defaultValue = "1") Integer nowPage,
                                   @RequestParam(name = "state", required = false, defaultValue = "all") String state, // all, recruiting, done
                                   @RequestParam(name = "order", required = false, defaultValue = "recent") String order, // recent, comment, bookmark
                                   @RequestParam(name = "search", required = false) String searchWord,
                                   @RequestParam(name = "tags", required = false) String searchTag,
                                   Model model) {

        try {
            int realPageNum = nowPage - 1;

            // 넘어온 정렬 기준값 -> 컬럼명으로 변환
            order = convertToProperty(order);

            // 현재 페이지, 정렬 기준 컬럼명으로 Pageable 인스턴스
            Pageable pageable = PageRequest.of(realPageNum, 5, Sort.by(order).descending());

            Page<?> data = null;
            if (type.equals("parties")) {
                data = partyService.getPartyPostList(pageable, state, searchWord, searchTag);
            } else {
                data = chatService.getChatPostList(pageable, searchWord, searchTag);
            }

            model.addAttribute("nowType", type);

            model.addAttribute("order", order);
            model.addAttribute("nowPage", nowPage);

            model.addAttribute("currentSearchWord", searchWord);
            model.addAttribute("currentSearchTag", searchTag);

            if (type.equals("parties")) {
                model.addAttribute("nowState", state);
            }

            model.addAttribute("endPage", data.getTotalPages()); // 총 페이지 수
            model.addAttribute("posts", data); // 데이터

            return "community/communityList";

        } catch (Exception e) {
            log.error("게시글 목록 불러오기 실패: {}", e.getMessage());
            model.addAttribute("errorMsg", e.getMessage());
            return "error/errorPage";
        }
    }

    /*
     * 넘어온 정렬 기준 -> 실제 DB 컬럼명으로 변환
     * */
    private String convertToProperty(String order) {
        switch (order) {
            case "recent" -> order = "createdAt";
            case "comment" -> order = "commentCount";
            case "bookmark" -> order = "bookmarkCount";
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

            Map<String, Object> result = communityService.getCommunityPost(communityId, false, request);
            CommunityDetailResponse post = (CommunityDetailResponse) result.get("post");


            model.addAttribute("post", post);
            model.addAttribute("cmts", result.get("cmts"));
            model.addAttribute("kakaoApiKey", kakaoApiKey);

            return "community/communityDetail";

        } catch (CustomException e) {
            log.error("상세글 불러오기 실패: {}", e.getCustomErrorCode().getMessage());
            response.setStatus(e.getCustomErrorCode().getStatus().value());
            model.addAttribute("message", e.getCustomErrorCode().getMessage());
            return "404";

        } catch (Exception e) {
            log.error("상세글 불러오기 실패: [서버 오류] {}", e.getMessage());
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            model.addAttribute("errorMsg", e.getMessage());
            return "error/errorPage";
        }
    }


    /*
     * 게시글 삭제
     * */
    @DeleteMapping("/boards/{type}/{communityId}")
    public ResponseEntity<?> deleteCommunity(@PathVariable("communityId") Long communityId,
                                              @PathVariable("type") String type,
                                              @CookieValue("access_token") Cookie accessToken) {

        try {
            communityService.deleteCommunityPost(accessToken.getValue(), communityId);
            return new ResponseEntity<>("/community/boards/" + type, HttpStatus.OK);

        } catch (CustomException e) {
            log.error("게시글 삭제 실패: {}", e.getCustomErrorCode().getMessage());
            return new ResponseEntity<>(e.getCustomErrorCode().getMessage(), e.getCustomErrorCode().getStatus());

        } catch (Exception e) {
            log.error("게시글 삭제 실패: [서버 오류] {}", e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
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
