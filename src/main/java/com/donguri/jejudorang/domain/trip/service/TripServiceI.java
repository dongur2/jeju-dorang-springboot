package com.donguri.jejudorang.domain.trip.service;

import com.donguri.jejudorang.domain.trip.dto.TripApiDataDto;
import com.donguri.jejudorang.domain.trip.dto.response.TripDetailResponseDto;
import com.donguri.jejudorang.domain.trip.dto.response.TripListResponseDto;
import com.donguri.jejudorang.domain.trip.entity.Trip;
import com.donguri.jejudorang.domain.trip.repository.TripRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class TripServiceI implements TripService{
    @Autowired
    private TripRepository tripRepository;

    @Override
    public Page<TripListResponseDto> getAllTripsOnPage(Pageable pageable) {
        Page<Trip> tripEntityList =  tripRepository.findAll(pageable);
        return tripEntityList.map(TripListResponseDto::new);
    }

    @Override
    public TripDetailResponseDto getTripDetail(Long tripId) {
        Trip tripEntity = tripRepository.findById(tripId).get();
        return new TripDetailResponseDto(tripEntity);
    }

    @Override
    public void saveApiTrips(Mono<TripApiDataDto> response) {
        // Dto -> Entity
        List<Trip> data = response
                .map(TripApiDataDto::toEntity)
                .block();

        // Entity -> DB
        tripRepository.saveAll(data);
    }
}
