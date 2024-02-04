package com.donguri.jejudorang.domain.trip.service;

import com.donguri.jejudorang.domain.trip.dto.TripApiDataDto;
import com.donguri.jejudorang.domain.trip.dto.response.TripDetailResponseDto;
import com.donguri.jejudorang.domain.trip.dto.response.TripListResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Mono;


public interface TripService {

    void saveApiTrips(Mono<TripApiDataDto> response);

    Page<TripListResponseDto> getAllTripsOnPage(Pageable pageable);

    // 여행 상세글 조회
    TripDetailResponseDto getTripDetail(String accessToken, Long tripId);
}
