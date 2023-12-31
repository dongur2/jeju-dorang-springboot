package com.donguri.jejudorang.domain.trip.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Entity @Getter @RequiredArgsConstructor
public class Trip {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "trip_id")
    private Long id; // PK
    private String placeId; // CNTS_300000000015996
    private String category; // 쇼핑
    private String name; // 장소 이름
    private String introduction; // 소개
    private String address; // 도로명 주소: 제주특별자치도 제주시 구좌읍 구좌로 51-1
    private String tel; // 064-766-3080
    private String tags; // 구좌읍, 세화리, 소품샵, 핸드메이드, 잡화, 레진공예
    private String thumbnail; // url
    private String image; // url
    private int likeCount; // 북마크 수

}
