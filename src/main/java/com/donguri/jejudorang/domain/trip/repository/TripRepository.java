package com.donguri.jejudorang.domain.trip.repository;

import com.donguri.jejudorang.domain.trip.entity.Trip;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TripRepository extends JpaRepository<Trip, Long> {

    // 태그 검색
    Page<Trip> findByTagsContaining(String word, Pageable pageable);

    // 카테고리별 정렬
    Page<Trip> findAllByCategory(String category, Pageable pageable);

    // 태그 검색 + 카테고리별 정렬
    Page<Trip> findByCategoryAndTagsContaining(String category, String word, Pageable pageable);
}
