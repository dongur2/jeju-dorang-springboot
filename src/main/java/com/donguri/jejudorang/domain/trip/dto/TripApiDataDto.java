package com.donguri.jejudorang.domain.trip.dto;

import com.donguri.jejudorang.domain.trip.entity.Trip;

import java.util.List;
import java.util.stream.Collectors;

public record TripApiDataDto(
    String result,
    String resultMessage,
    int totalCount,
    int resultCount,
    int pageCount,
    int currentPage,
    List<Item> items
) {
    public record Item(
            String alltag,
            String contentsid,
            ContentCategory contentscd,
            ContentRegion region1cd,
            String title,
            String roadaddress,
            String introduction,
            String phoneno,
            RepPhoto repPhoto
            ) {
       public record ContentCategory (
               String label
       ) {}

        public record ContentRegion (
                String label
        ) {}

       public record RepPhoto (
               PhotoId photoid
       ) {
           public record PhotoId (
                   String imgpath,
                   String thumbnailpath
           ) {}
       }
    }

    // items의 item을 Trip Entity로 변환해 리스트로 반환
    public List<Trip> toEntity() {
        return items.stream()
                .map(TripApiDataDto::mapToEntity)
                .collect(Collectors.toList());
    }

    private static Trip mapToEntity(TripApiDataDto.Item item) {
        return Trip.builder()
                .placeId(item.contentsid)
                .category(item.contentscd.label)
                .region(item.region1cd.label)
                .name(item.title)
                .introduction(item.introduction)
                .address(item.roadaddress)
                .tel(item.phoneno)
                .tags(item.alltag)
                .thumbnail(item.repPhoto.photoid.thumbnailpath)
                .image(item.repPhoto.photoid.imgpath)
                .build();
    }
}
