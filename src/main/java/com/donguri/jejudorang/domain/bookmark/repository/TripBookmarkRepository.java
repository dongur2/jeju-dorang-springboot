package com.donguri.jejudorang.domain.bookmark.repository;

import com.donguri.jejudorang.domain.bookmark.entity.TripBookmark;
import com.donguri.jejudorang.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TripBookmarkRepository extends JpaRepository<TripBookmark, Long> {
    Optional<TripBookmark> findByUserAndTripId(User user, Long tripId);
}