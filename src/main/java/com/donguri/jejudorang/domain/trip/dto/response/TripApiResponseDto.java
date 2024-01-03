package com.donguri.jejudorang.domain.trip.dto.response;

import java.util.List;

public record TripApiResponseDto(
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
            ContentCategory region2cd,
            String title,
            String roadaddress,
            String introduction,
            String phoneno,
            RepPhoto repPhoto
            ) {
       public record ContentCategory (
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
}
