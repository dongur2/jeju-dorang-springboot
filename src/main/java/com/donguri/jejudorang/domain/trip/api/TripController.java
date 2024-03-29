package com.donguri.jejudorang.domain.trip.api;

import com.donguri.jejudorang.domain.trip.api.swagger.TripControllerDocs;
import com.donguri.jejudorang.domain.trip.dto.response.TripDetailResponseDto;
import com.donguri.jejudorang.domain.trip.dto.response.TripListResponseDto;
import com.donguri.jejudorang.domain.trip.service.TripService;
import com.donguri.jejudorang.global.error.CustomException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/trip")
public class TripController implements TripControllerDocs {
    private final TripService tripService;


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
                              @RequestParam(name = "nowPage", required = false, defaultValue = "1") Integer nowPage,
                              @RequestParam(name = "category", required = false, defaultValue = "전체") String category,
                              Model model, HttpServletResponse response) {
        try {
            int realPageNum = nowPage - 1;

            Pageable pageable = PageRequest.of(realPageNum, 10);
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
            if(result.getTotalPages() < 1) {
                model.addAttribute("endPage", 0);
            } else {
                model.addAttribute("endPage", result.getTotalPages() + 1);
            }
            return "trip/tripList";

        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            model.addAttribute("errorMsg", e.getMessage());
            return "error/errorPage";
        }
    }

    @GetMapping("/places/{placeId}")
    public String tripDetail(@PathVariable("placeId") Long placeId,
                             @CookieValue(required = false, name = "access_token") Cookie accessToken,
                             Model model, HttpServletResponse response) {
        try {
            String token = null;
            if(accessToken != null) {
                token = accessToken.getValue();
            }

            TripDetailResponseDto tripDetail = tripService.getTripDetail(token, placeId);
            model.addAttribute("trip", tripDetail);
            return "trip/tripDetail";

        } catch (CustomException e) {
            log.error("여행 상세글 조회 실패: {}", e.getCustomErrorCode().getMessage());
            response.setStatus(e.getCustomErrorCode().getStatus().value());
            model.addAttribute("message", e.getCustomErrorCode().getMessage());
            return "error/404";
        }
    }

}
