package com.donguri.jejudorang.domain.trip.dto.response;

import com.donguri.jejudorang.domain.trip.entity.Trip;
import lombok.Builder;
import lombok.Getter;

@Getter
public class TripDetailResponseDto {
    private Long id;
    private String category;
    private String name;
    private String introduction;
    private String address;
    private String tel;
    private String tags;
    private String image;

    @Builder
    public TripDetailResponseDto(Trip trip) {
        this.id = trip.getId();
        this.category = trip.getCategory();
        this.name = trip.getName();
        this.introduction = trip.getIntroduction();
        this.address = trip.getAddress();
        this.tel = trip.getTel();
        this.tags = trip.getTags();
        this.image = trip.getImage();
    }
}
