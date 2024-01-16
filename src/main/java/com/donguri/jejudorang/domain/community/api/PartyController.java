package com.donguri.jejudorang.domain.community.api;

import com.donguri.jejudorang.domain.community.dto.response.PartyDetailResponseDto;
import com.donguri.jejudorang.domain.community.service.PartyService;
import jakarta.servlet.http.HttpServletRequest;
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
    * Query Parameters: page, state, order, (tag, search)
    *
    * */
    @GetMapping
    public String getPartyList(@RequestParam(name = "page", required = false, defaultValue = "0") Integer nowPage,
                                            @RequestParam(name = "state", required = false) String state, // recruiting, done
                                            @RequestParam(name = "order", required = false, defaultValue = "recent") String order, // recent, comment, bookmark
                                            Model model) {

        // 넘어온 정렬 기준값 -> 컬럼명으로 변환
        order = convertToProperty(order);
        // 현재 페이지, 정렬 기준 컬럼명으로 Pageable 인스턴스
        Pageable pageable = PageRequest.of(nowPage, 5, Sort.by(order).descending());

        Map<String, Object> partyListInMap = partyService.getPartyPostList(pageable, state);

        // 뷰로 함께 리턴
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
