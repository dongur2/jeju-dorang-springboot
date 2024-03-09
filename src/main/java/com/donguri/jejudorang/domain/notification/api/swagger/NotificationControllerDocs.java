package com.donguri.jejudorang.domain.notification.api.swagger;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Tag(name = "Notification", description = "알림 관련 API")
public interface NotificationControllerDocs {

    @Parameter(name = "access_token", description = "사용자의 액세스 토큰", required = true, schema = @Schema(type = "string", format = "JWT"))
    @Operation(summary = "SseEmitter 생성 & 연결", description = "사용자의 액세스 토큰을 기반으로 실시간 알림을 위해 현재 사용자에게 연결된 SseEmitter 인스턴스를 생성합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "SseEmitter 생성 성공"),
            @ApiResponse(responseCode = "500", description = "SseEmitter 생성 실패: 서버 에러 발생")
    })
    SseEmitter connect(@CookieValue("access_token") Cookie token);

    @Parameter(name = "access_token", description = "사용자의 액세스 토큰", required = true, schema = @Schema(type = "string", format = "JWT"))
    @Operation(summary = "알림 조회", description = "현재 사용자에게 발생한 알림을 최신순으로 정렬하여 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "알림 조회 성공"),
            @ApiResponse(responseCode = "204", description = "알림 조회 성공: 새로운 알림/삭제하지 않은 알림이 없음"),
            @ApiResponse(responseCode = "500", description = "알림 조회 실패: 서버 에러 발생")
    })
    ResponseEntity<?> getNotifications(@CookieValue("access_token") Cookie token);


    @Parameter(name = "access_token", description = "사용자의 액세스 토큰", required = true, schema = @Schema(type = "string", format = "JWT"))
    @Operation(summary = "알림 읽음 처리", description = "알림 카드를 클릭해 상세글로 이동할 경우, 해당 알림을 읽음 처리 합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "알림 읽음 처리 성공: 해당 알림의 상세글 URI를 함께 반환합니다."),
            @ApiResponse(responseCode = "403", description = "알림 읽음 처리 실패: 존재하지 않는 알림"),
            @ApiResponse(responseCode = "404", description = "알림 읽음 처리 실패: 사용자 권한 없음"),
            @ApiResponse(responseCode = "500", description = "알림 읽음 처리 실패: 서버 에러 발생")
    })
    ResponseEntity<?> updateNotificationCheck(@CookieValue("access_token") Cookie token, @RequestParam(name = "alertId") Long alertId);

    @Parameter(name = "access_token", description = "사용자의 액세스 토큰", required = true, schema = @Schema(type = "string", format = "JWT"))
    @Operation(summary = "알림 삭제", description = "해당 알림을 데이터베이스에서 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "알림 삭제 성공"),
            @ApiResponse(responseCode = "403", description = "알림 삭제 실패: 존재하지 않는 알림"),
            @ApiResponse(responseCode = "404", description = "알림 삭제 실패: 사용자 권한 없음"),
            @ApiResponse(responseCode = "500", description = "알림 삭제 실패: 서버 에러 발생")
    })
    ResponseEntity<HttpStatus> deleteNotification(@CookieValue("access_token") Cookie token, @RequestParam(name = "alertId") Long alertId);
}
