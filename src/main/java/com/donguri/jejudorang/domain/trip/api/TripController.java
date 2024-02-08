package com.donguri.jejudorang.domain.trip.api;

import com.donguri.jejudorang.domain.trip.dto.response.TripDetailResponseDto;
import com.donguri.jejudorang.domain.trip.dto.response.TripListResponseDto;
import com.donguri.jejudorang.domain.trip.service.TripService;
import jakarta.servlet.http.Cookie;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;


@Slf4j
@Controller
@RequestMapping("/trip")
public class TripController {
    @Autowired private final TripService tripService;

    public TripController(TripService tripService) {
        this.tripService = tripService;
    }


    /*
     * GET
     * 여행 메인
     * 여행 글 목록 조회: 태그 검색
     *
     * > String word: 검색어
     * > Integer nowPage: 현재 페이지 번호
     *
    * */
    @GetMapping("/lists")
    public String getTripList(@RequestParam(name = "search", required = false) String word,
                              @RequestParam(name = "nowPage", required = false, defaultValue = "0") Integer nowPage,
                              Model model) {

        try {
            Pageable pageable = PageRequest.of(nowPage, 10);
            Map<String, Object> result;

            // 검색어가 존재할 경우 검색 메서드 호출, 없을 경우 전체 데이터 조회 메서드 호출
            if(word != null) {
                result = tripService.getSearchedTripsContainingTagKeyword(word, pageable);
            } else {
                result = tripService.getAllTrips(pageable);
            }

            model.addAttribute("nowPage", nowPage);
            model.addAttribute("searchWord", word);

            model.addAttribute("trips", result.get("data"));

            // 데이터가 없을 경우 페이지 수 null -> 0 처리
            if(result.get("page") == null) {
                model.addAttribute("endPage", 0);
            } else {
                model.addAttribute("endPage", result.get("page"));
            }

            return "/trip/tripList";

        } catch (Exception e) {
            log.error("여행 리스트 데이터 불러오기 실패: {}", e.getMessage());
            model.addAttribute("errorMsg", e.getMessage());
            return "/error/errorTemp";
        }

    }

    @GetMapping("/places/{placeId}")
    public String tripDetail(@PathVariable("placeId") Long placeId,
                             @CookieValue(required = false, name = "access_token") Cookie accessToken,
                             Model model) {

        try {
            String token = null;
            if(accessToken != null) {
                token = accessToken.getValue();
            }

            TripDetailResponseDto tripDetail = tripService.getTripDetail(token, placeId);
            model.addAttribute("trip", tripDetail);
            return "/trip/tripDetail";

        } catch (Exception e) {
            log.error("여행 상세글 조회에 실패했습니다. {}", e.getMessage());
            model.addAttribute("errorMsg", e.getMessage());
            return "/error/errorTemp";
        }
    }

}
