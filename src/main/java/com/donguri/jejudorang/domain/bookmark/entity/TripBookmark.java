package com.donguri.jejudorang.domain.bookmark.entity;

import com.donguri.jejudorang.domain.trip.entity.Trip;
import com.donguri.jejudorang.domain.user.entity.User;
import com.donguri.jejudorang.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class TripBookmark extends BaseEntity {

    @Id
    @Column(nullable = false, name = "trip_bookmark_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "user_id")
    @ManyToOne
    private User user;

    @JoinColumn(nullable = false, name = "trip_id")
    @ManyToOne
    private Trip trip;

    @Builder
    public TripBookmark(User user, Trip trip) {
        this.user = user;
        this.trip = trip;
    }
}
