package com.donguri.jejudorang.domain.trip.service;

import com.donguri.jejudorang.domain.trip.entity.Trip;

import java.util.List;

public interface TripService {
    void saveApiTrips(List<Trip> trips);
}
