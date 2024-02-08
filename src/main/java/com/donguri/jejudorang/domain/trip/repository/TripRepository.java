package com.donguri.jejudorang.domain.trip.repository;

import com.donguri.jejudorang.domain.trip.entity.Trip;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TripRepository extends JpaRepository<Trip, Long> {

    Page<Trip> findByTagsContaining(String word, Pageable pageable);
}
