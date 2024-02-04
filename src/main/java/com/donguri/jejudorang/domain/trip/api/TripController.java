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
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;


@Slf4j
@Controller
@RequestMapping("/trip")
public class TripController {
    @Autowired private final TripService tripService;

    public TripController(TripService tripService) {
        this.tripService = tripService;
    }


    @GetMapping("/lists/{nowPage}")
    public String tripHome(@PathVariable("nowPage") Integer nowPage, Model model) {
        Pageable pageable = PageRequest.of(nowPage, 10);
        Map<String, Object> result = tripService.getAllTripsOnPage(pageable);

        model.addAttribute("trips", result.get("trips"));
        model.addAttribute("totalPage", result.get("totalPages"));
        return "/trip/tripList";
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
