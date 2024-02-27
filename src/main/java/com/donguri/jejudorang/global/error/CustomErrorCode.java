package com.donguri.jejudorang.global.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CustomErrorCode {

    /*
     * 404
     * */
    TRIP_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 여행지를 찾을 수 없습니다.");

    private final HttpStatus status;
    private final String message;
}
