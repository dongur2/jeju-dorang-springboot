package com.donguri.jejudorang.domain.community.api;

import com.donguri.jejudorang.domain.community.dto.response.PartyDetailResponseDto;
import com.donguri.jejudorang.domain.community.service.PartyService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.donguri.jejudorang.domain.community.api.CommunityController.convertToProperty;

@Slf4j
@Controller
@RequestMapping("/community/parties")
public class PartyController {
    @Autowired
    private PartyService partyService;

    @Value("${kakao-api-key}")
    private String kakaoApiKey;

    @Value("${view.cookie-expire}")
    private int viewCookieTime;


    /*
    * 모임글 목록
    * /community/parties
    * GET
    *
    * > Parameters
    * @RequestParam
    * Integer page: 현재 페이지
    * String state: 정렬 기준 모집 상태
    * String order: 정렬 기준
    * String search: 검색어
    * String searchTag: 검색 태그 (A,B,C,...,N개)
    *
    *
    * > Return Model Attributes
    * String currentSearchWord: 목록 정렬한 모집 상태 - all / recruiting / done
    * String searchWord: 검색어
    * int allPartyPageCount: 목록 전체 페이지 수
    * Page<Community> partyListDtoPage: 글 데이터
    *
    * */
    @GetMapping
    public String getPartyList(@RequestParam(name = "page", required = false, defaultValue = "0") Integer nowPage,
                               @RequestParam(name = "state", required = false, defaultValue = "all") String state, // all, recruiting, done
                               @RequestParam(name = "order", required = false, defaultValue = "recent") String order, // recent, comment, bookmark
                               @RequestParam(name = "search", required = false) String searchWord,
                               @RequestParam(name = "tags", required = false) String searchTag,
                               Model model) {

        // 넘어온 정렬 기준값 -> 컬럼명으로 변환
        order = convertToProperty(order);
        // 현재 페이지, 정렬 기준 컬럼명으로 Pageable 인스턴스
        Pageable pageable = PageRequest.of(nowPage, 5, Sort.by(order).descending());

        Map<String, Object> partyListInMap = partyService.getPartyPostList(pageable, state, searchWord, searchTag);

        model.addAttribute("nowState", state);
        model.addAttribute("order", order);
        model.addAttribute("nowPage", nowPage);
        model.addAttribute("currentSearchWord", searchWord);
        model.addAttribute("currentSearchTag", searchTag);

        model.addAttribute("allPartyPageCount", partyListInMap.get("allPartyPageCount")); // 총 페이지 수
        model.addAttribute("partyListDtoPage", partyListInMap.get("partyListDtoPage")); // 데이터
        return "/community/communityPartyList";
    }


    /*
     * 상세글 조회
     * /community/parties/{communityId}
     * GET
     *
     * */
    @GetMapping("/{communityId}")
    public String getPartyDetail(@PathVariable("communityId") Long communityId,
                                 HttpServletRequest request, HttpServletResponse response,
                                 Model model) {

        // 쿠키 체크 & 조회수 업데이트 여부 결정 & 조건 충족할 경우 조회수, 쿠키 업데이트
        checkIsAlreadyReadForUpdateView(communityId, request, response);

        PartyDetailResponseDto foundPartyPost = partyService.getPartyPost(communityId);

        model.addAttribute("post", foundPartyPost);
        model.addAttribute("kakaoApiKey", kakaoApiKey);
        return "/community/communityDetail";
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

            partyService.updatePartyView(communityId);
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

                partyService.updatePartyView(communityId);
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


    /*
    * 모집상태 변경
    * /community/parties/{communityId}/state
    *
    * */
    @PutMapping("/{communityId}/state")
    public ResponseEntity<String> modifyBoardJoinState(@PathVariable("communityId") Long communityId) {
        try {
            partyService.changePartyJoinState(communityId);
            return ResponseEntity.ok("상태 변경 완료");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred");
        }
    }

}
