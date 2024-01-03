package com.donguri.jejudorang.domain.trip.dto.response;

import com.donguri.jejudorang.domain.trip.entity.Trip;
import lombok.Builder;
import lombok.Getter;

@Getter
public class TripListResponseDto {
    private Long id;
    private String category;
    private String name;
    private String introduction;
    private String tags;
    private String thumbnail;

    @Builder
    public TripListResponseDto(Trip trip) {
        this.id = trip.getId();
        this.category = trip.getCategory();
        this.name = trip.getName();
        this.introduction = trip.getIntroduction();
        this.tags = trip.getTags();
        this.thumbnail = trip.getThumbnail();
    }
}
