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
     * > String category: 카테고리
     *
    * */
    @GetMapping("/lists")
    public String getTripList(@RequestParam(name = "search", required = false) String word,
                              @RequestParam(name = "nowPage", required = false, defaultValue = "0") Integer nowPage,
                              @RequestParam(name = "category", required = false, defaultValue = "전체") String category,
                              Model model) {

        try {
            Pageable pageable = PageRequest.of(nowPage, 10);
            Page<TripListResponseDto> result;

            // 검색어가 없을 경우
            if(word == null || word.trim().isEmpty()) {
                if (category.equals("전체")) { // 전체 카테고리
                    result = tripService.getAllTrips(pageable);
                } else { // 그 외 카테고리
                    result = tripService.getAllTripsInCategory(category, pageable);
                }

            // 검색어가 존재할 경우 검색 메서드 호출, 없을 경우 전체 데이터 조회 메서드 호출
            } else {
                if (category.equals("전체")) { // 전체 카테고리
                    result = tripService.getSearchedTripsContainingTagKeyword(word, pageable);
                } else { // 그 외 카테고리
                    result = tripService.getSearchedTripsContainingTagKeywordInCategory(word, category, pageable);
                }
            }

            model.addAttribute("nowPage", nowPage);
            model.addAttribute("searchWord", word);
            model.addAttribute("nowCategory", category);

            model.addAttribute("trips", result);

            // 데이터가 없을 경우 페이지 수 null -> 0 처리
            if(result == null) {
                model.addAttribute("endPage", 0);
            } else {
                model.addAttribute("endPage", result.getTotalPages());
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
