package com.donguri.jejudorang.domain.trip.service;

import com.donguri.jejudorang.domain.trip.dto.TripApiDataDto;
import com.donguri.jejudorang.domain.trip.dto.response.TripDetailResponseDto;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Mono;

import java.util.Map;


public interface TripService {

    // 제주도 여행 API 데이터 저장
    void saveApiTrips(Mono<TripApiDataDto> response);


    // 전체 여행 데이터 조회
    Map<String, Object> getAllTrips(Pageable pageable);

    // 검색한 여행 데이터 조회: 태그에 검색어 포함
    Map<String, Object> getSearchedTripsContainingTagKeyword(String word, Pageable pageable);

    // 여행 상세글 조회
    TripDetailResponseDto getTripDetail(String accessToken, Long tripId);

}
