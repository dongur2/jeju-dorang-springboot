package com.donguri.jejudorang.domain.notification.api;

import com.donguri.jejudorang.domain.notification.api.swagger.NotificationControllerDocs;
import com.donguri.jejudorang.domain.notification.dto.NotificationResponse;
import com.donguri.jejudorang.domain.notification.service.NotificationService;
import com.donguri.jejudorang.global.error.CustomException;
import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/notifications")
public class NotificationController implements NotificationControllerDocs {
    private final NotificationService notificationService;


    @Transactional
    @GetMapping("/connect")
    public SseEmitter connect(@CookieValue("access_token") Cookie token) {
        try {
            return notificationService.connectNotification(token.getValue());

        } catch (Exception e) {
            log.error("SSE 연결 실패: {}", e.getMessage());
            return null;
        }
    }

    @Transactional
    @GetMapping
    public ResponseEntity<?> getNotifications(@CookieValue("access_token") Cookie token) {
        try {
            List<NotificationResponse> notifications = notificationService.getNotifications(token.getValue());
            return ResponseEntity.ok(notifications);

        } catch (CustomException e) {
            log.error("알림 조회 실패: {}", e.getCustomErrorCode().getMessage());
            return new ResponseEntity<>(e.getCustomErrorCode().getMessage(), e.getCustomErrorCode().getStatus());

        } catch (Exception e) {
            log.error("알림 조회 실패: [서버 오류] {}", e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    @PutMapping("/check")
    public ResponseEntity<?> updateNotificationCheck(@CookieValue("access_token") Cookie token, @RequestParam(name = "alertId") Long alertId) {
        try {
            return new ResponseEntity<>(notificationService.updateNotificationToChecked(token.getValue(), alertId), HttpStatus.OK);

        } catch (CustomException e) {
            log.error("알림 상태 업데이트 및 리다이렉트 실패: {}", e.getCustomErrorCode().getMessage());
            return new ResponseEntity<>(e.getCustomErrorCode().getMessage(), e.getCustomErrorCode().getStatus());

        } catch (Exception e) {
            log.error("알림 상태 업데이트 및 리다이렉트 실패: [서버 오류] {}", e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    @DeleteMapping
    public ResponseEntity<HttpStatus> deleteNotification(@CookieValue("access_token") Cookie token,
                                                         @RequestParam(name = "alertId") Long alertId) {
        try {
            notificationService.deleteNotification(token.getValue(), alertId);
            return new ResponseEntity<>(HttpStatus.OK);

        } catch (CustomException e) {
            log.error("알림 삭제 실패: {}", e.getCustomErrorCode().getMessage());
            return new ResponseEntity<>(e.getCustomErrorCode().getStatus());

        } catch (Exception e) {
            log.error("알림 삭제 실패: [서버 오류] {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
