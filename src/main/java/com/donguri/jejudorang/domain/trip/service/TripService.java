package com.donguri.jejudorang.domain.trip.service;

import com.donguri.jejudorang.domain.trip.dto.TripApiDataDto;
import com.donguri.jejudorang.domain.trip.dto.response.TripDetailResponseDto;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Mono;

import java.util.Map;


public interface TripService {

    void saveApiTrips(Mono<TripApiDataDto> response);

    Map<String, Object> getAllTripsOnPage(Pageable pageable);

    // 여행 상세글 조회
    TripDetailResponseDto getTripDetail(String accessToken, Long tripId);
}
