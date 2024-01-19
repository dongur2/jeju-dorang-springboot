package com.donguri.jejudorang.domain.trip.entity;

import com.donguri.jejudorang.global.common.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Entity @Getter @RequiredArgsConstructor
public class Trip extends BaseEntity {

    @Size(max = 20)
    private String placeId; // CNTS_300000000015996

    @Size(max = 10)
    private String category; // 쇼핑

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

    private int likeCount; // 북마크 수

    @Builder
    public Trip(String placeId, String category, String name, String introduction, String address, String tel, String tags, String thumbnail, String image) {
        this.placeId = placeId;
        this.category = category;
        this.name = name;
        this.introduction = introduction;
        this.address = address;
        this.tel = tel;
        this.tags = tags;
        this.thumbnail = thumbnail;
        this.image = image;
    }
}
