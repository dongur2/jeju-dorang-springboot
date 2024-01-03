package com.donguri.jejudorang.domain.trip.service;

import com.donguri.jejudorang.domain.trip.dto.TripApiDataDto;
import com.donguri.jejudorang.domain.trip.dto.response.TripListResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Mono;


public interface TripService {
    void saveApiTrips(Mono<TripApiDataDto> response);
    Page<TripListResponseDto> findAll(Pageable pageable);
}
