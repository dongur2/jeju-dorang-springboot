package com.donguri.jejudorang.domain.trip.service;

import com.donguri.jejudorang.domain.trip.entity.Trip;
import com.donguri.jejudorang.domain.trip.repository.TripRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TripServiceI implements TripService{
    @Autowired
    private TripRepository tripRepository;

    @Override
    public void saveApiTrips(List<Trip> trips) {
        tripRepository.saveAll(trips);
    }
}
