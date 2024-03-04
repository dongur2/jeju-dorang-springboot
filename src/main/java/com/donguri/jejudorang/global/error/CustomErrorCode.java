package com.donguri.jejudorang.global.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CustomErrorCode {

    /*
     * 204 요청이 성공했지만 응답에는 컨텐츠가 없음
     * */
    NO_NOTIFICATION(HttpStatus.NO_CONTENT, "새 알림이 없습니다."),
    NOT_BOOKMARKED(HttpStatus.NO_CONTENT, "북마크한 글이 아닙니다."),

    /*
     * 400 데이터의 불일치; 클라이언트가 제공한 데이터가 잘못되었거나 누락됨
     * */
    NO_REQUEST_IMAGE(HttpStatus.BAD_REQUEST, "첨부된 이미지가 없습니다."),
    PASSWORD_MISMATCH(HttpStatus.BAD_REQUEST, "입력한 비밀번호가 일치하지 않습니다."),
    EMAIL_AUTH_CODE_EXPIRED(HttpStatus.BAD_REQUEST, "인증번호가 만료되었습니다."),

    /*
     * 401 요청된 리소스에 대한 유효한 인증 자격 증명이 없기 때문에 클라이언트 요청이 완료되지 않았음
     * 사용자 인증을 통해 리소스에 대한 액세스를 허용 가능
     * */
    INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, "현재 비밀번호가 일치하지 않습니다."),

    /*
     * 403 클라이언트가 서버에 요청 -> 서버가 해당 요청을 거부; 클라이언트가 해당 권한 없음
     * */
    PERMISSION_ERROR(HttpStatus.FORBIDDEN, "권한이 없습니다."),

    /*
     * 404 서버가 요청받은 리소스를 찾을 수 없음
     * */
    NOTIFICATION_NOT_FOUND(HttpStatus.NOT_FOUND, "알림이 존재하지 않습니다"),
    TRIP_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 여행지를 찾을 수 없습니다."),
    COMMUNITY_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 게시글을 찾을 수 없습니다."),
    ROLE_NOT_FOUND(HttpStatus.NOT_FOUND, "권한을 찾을 수 없습니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "유저를 찾을 수 없습니다."),

    /*
     * 409 리소스 간 충돌
     * */
    ALREADY_BOOKMARKED(HttpStatus.CONFLICT, "이미 북마크한 글입니다."),
    EMAIL_ALREADY_EXISTED(HttpStatus.CONFLICT, "이미 가입된 이메일입니다."),
    ID_ALREADY_EXISTED(HttpStatus.CONFLICT, "이미 가입된 아이디입니다."),

    /*
     * 413 요청 엔터티 > 서버에 의해 정의된 제한
     * */
    IMAGE_TOO_LARGE_FOR_PROFILE(HttpStatus.PAYLOAD_TOO_LARGE, "파일 크기는 1MB를 초과할 수 없습니다."),
    IMAGE_TOO_LARGE_FOR_COMMUNITY(HttpStatus.PAYLOAD_TOO_LARGE, "파일 크기는 3MB를 초과할 수 없습니다."),

    /*
     * 415 클라이언트가 보낸 페이로드가 지원하지 않는 형식이기 때문에 서버가 요청을 수락하지 않음
     * */
    NOT_SUPPORTED_TYPE_FOR_IMAGE(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "파일은 png, jpg, jpeg만 가능합니다."),

    /*
     * 500
     * */
    FAILED_TO_UPLOAD_IMAGE(HttpStatus.INTERNAL_SERVER_ERROR, "사진 업로드에 실패했습니다.");

    private final HttpStatus status;
    private final String message;
}
