package com.donguri.jejudorang.domain.trip.dto.response;

import com.donguri.jejudorang.domain.trip.entity.Trip;
import lombok.Builder;
import lombok.Getter;

@Getter
public class TripDetailResponseDto {
    private Long id;
    private String region;
    private String category;
    private String name;
    private String introduction;
    private String address;
    private String tel;
    private String tags;
    private String image;

    private boolean isBookmarked;

    @Builder
    public TripDetailResponseDto(Trip trip, String nowViewer) {
        this.id = trip.getId();
        this.region = trip.getRegion();
        this.category = trip.getCategory();
        this.name = trip.getName();
        this.introduction = trip.getIntroduction();
        this.address = trip.getAddress();
        this.tel = trip.getTel();
        this.tags = trip.getTags();
        this.image = trip.getImage();

        // 로그인 된 유저의 북마크 여부 확인
        if(trip.getBookmarks().stream()
                .anyMatch(bookmark ->
                        bookmark.getUser().getProfile().getExternalId().equals(nowViewer))) {
            this.isBookmarked = true;
        }
    }

    @Builder
    public TripDetailResponseDto(Trip trip) {
        this.id = trip.getId();
        this.region = trip.getRegion();
        this.category = trip.getCategory();
        this.name = trip.getName();
        this.introduction = trip.getIntroduction();
        this.address = trip.getAddress();
        this.tel = trip.getTel();
        this.tags = trip.getTags();
        this.image = trip.getImage();

        this.isBookmarked = false;
    }
}
