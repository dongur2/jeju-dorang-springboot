package com.donguri.jejudorang.global.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CustomErrorCode {

    /*
     * 400 데이터의 불일치; 클라이언트가 제공한 데이터가 잘못되었거나 누락됨
     * */
    PASSWORD_MISMATCH(HttpStatus.BAD_REQUEST, "비밀번호가 일치하지 않습니다."),
    EMAIL_AUTH_CODE_EXPIRED(HttpStatus.BAD_REQUEST, "인증번호가 만료되었습니다."),

    /*
     * 404
     * */
    TRIP_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 여행지를 찾을 수 없습니다."),

    /*
     * 409 리소스 간 충돌
     * */
    EMAIL_ALREADY_EXISTED(HttpStatus.CONFLICT, "이미 가입된 이메일입니다."),
    ID_ALREADY_EXISTED(HttpStatus.CONFLICT, "이미 가입된 아이디입니다."),

    /*
     * 500
     * */
    ROLE_NOT_FOUND(HttpStatus.INTERNAL_SERVER_ERROR, "해당 권한을 찾을 수 없습니다.");

    private final HttpStatus status;
    private final String message;
}
