package com.donguri.jejudorang.domain.trip.entity;

import com.donguri.jejudorang.domain.bookmark.entity.TripBookmark;
import com.donguri.jejudorang.global.common.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.Formula;

import java.util.HashSet;
import java.util.Set;

@Entity @Getter @RequiredArgsConstructor
public class Trip extends BaseEntity {

    @Id
    @Column(name = "trip_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Size(max = 20)
    private String placeId; // CNTS_300000000015996

    @Size(max = 10)
    private String category; // 쇼핑

    @Size(max = 10)
    private String region; // 시

    @Size(max = 50)
    private String name; // 장소 이름

    @Size(max = 1000)
    private String introduction; // 소개
    private String address; // 도로명 주소: 제주특별자치도 제주시 구좌읍 구좌로 51-1

    @Size(max = 50)
    private String tel; // 064-766-3080

    @Size(max = 500)
    private String tags; // 구좌읍, 세화리, 소품샵, 핸드메이드, 잡화, 레진공예

    @Size(max = 500)
    private String thumbnail; // url
    @Size(max = 500)
    private String image; // url

    @OneToMany(mappedBy = "trip"
            , cascade = CascadeType.ALL
            , orphanRemoval = true
            , fetch = FetchType.EAGER)
    private Set<TripBookmark> bookmarks = new HashSet<>();

    // 페이징 정렬 위한 가상 컬럼
    @Formula("(SELECT COUNT(*) FROM trip_bookmark t WHERE t.trip_id = trip_id)")
    private int bookmarksCount;

    @Builder
    public Trip(String placeId, String category, String region, String name, String introduction, String address, String tel, String tags, String thumbnail, String image) {
        this.placeId = placeId;
        this.category = category;
        this.region = region;
        this.name = name;
        this.introduction = introduction;
        this.address = address;
        this.tel = tel;
        this.tags = tags;
        this.thumbnail = thumbnail;
        this.image = image;
    }

    // 북마크 업데이트
    public void updateBookmarks(TripBookmark bookmark) {
        if (bookmarks.contains(bookmark)) {
            bookmarks.remove(bookmark);
        } else {
            bookmarks.add(bookmark);
        }
    }
}
