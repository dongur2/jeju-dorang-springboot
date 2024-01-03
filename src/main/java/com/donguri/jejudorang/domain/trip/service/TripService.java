package com.donguri.jejudorang.domain.trip.service;

import com.donguri.jejudorang.domain.trip.dto.TripApiDataDto;
import com.donguri.jejudorang.domain.trip.entity.Trip;
import reactor.core.publisher.Mono;

import java.util.List;

public interface TripService {
    void saveApiTrips(Mono<TripApiDataDto> response);
    List<Trip> findAll();
}
