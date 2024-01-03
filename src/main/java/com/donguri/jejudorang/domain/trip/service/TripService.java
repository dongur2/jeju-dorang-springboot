package com.donguri.jejudorang.domain.trip.service;

import com.donguri.jejudorang.domain.trip.dto.TripApiDataDto;
import reactor.core.publisher.Mono;

import java.util.List;

public interface TripService {
    void saveApiTrips(Mono<TripApiDataDto> response);
}
