package com.donguri.jejudorang.domain.community.api;

import com.donguri.jejudorang.domain.community.service.CommunityService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

import static com.donguri.jejudorang.domain.community.api.CommunityController.convertToProperty;

@Slf4j
@Controller
@RequestMapping("/community/parties")
public class PartyController {
    @Autowired
    private CommunityService communityService;

    @Value("${kakao-api-key}")
    private String kakaoApiKey;

    @GetMapping
    @ResponseBody
    public Map<String, Object> getPartyList(@RequestParam(name = "page", required = false, defaultValue = "0") Integer nowPage,
                                            @RequestParam(name = "state", required = false) String state, // recruiting, done
                                            @RequestParam(name = "order", required = false, defaultValue = "recent") String order, // recent, comment, liked
                                            Model model) {

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

}
