package com.donguri.jejudorang.domain.community.api;

import com.donguri.jejudorang.domain.community.dto.response.PartyDetailResponseDto;
import com.donguri.jejudorang.domain.community.service.PartyService;
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

import java.util.Map;

import static com.donguri.jejudorang.domain.community.api.CommunityController.convertToProperty;

@Slf4j
@Controller
@RequestMapping("/community/parties")
public class PartyController {
    @Autowired
    private PartyService partyService;

    @Value("${kakao-api-key}")
    private String kakaoApiKey;


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

        log.info("order={}, state={}, search={}, searchTag={}", order,state,searchWord, searchTag);

        Map<String, Object> partyListInMap = partyService.getPartyPostList(pageable, state, searchWord, searchTag);

        model.addAttribute("nowState", state);
        model.addAttribute("currentSearchWord", searchWord);

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
    public String getPartyDetail(@PathVariable("communityId") Long communityId, Model model) {
        PartyDetailResponseDto foundPartyPost = partyService.getPartyPost(communityId);

        model.addAttribute("post", foundPartyPost);
        model.addAttribute("kakaoApiKey", kakaoApiKey);
        return "/community/communityDetail";
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
