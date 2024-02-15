package com.donguri.jejudorang.domain.notification.api;

import com.donguri.jejudorang.domain.notification.dto.NotificationResponse;
import com.donguri.jejudorang.domain.notification.service.NotificationService;
import jakarta.servlet.http.Cookie;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;


@Slf4j
@RestController
@RequestMapping("/notifications")
public class NotificationController {

    @Autowired private final NotificationService notificationService;
    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping("/connect")
    public SseEmitter connect(@CookieValue("access_token") Cookie token) {
        try {
            return notificationService.connectNotification(token.getValue());

        } catch (Exception e) {
            log.error("SSE 연결 실패: {}", e.getMessage());
            return null;
        }
    }

    @GetMapping
    public ResponseEntity<?> getNotifications(@CookieValue("access_token") Cookie token) {
        try {
            List<NotificationResponse> notifications = notificationService.getNotifications(token.getValue());
            return ResponseEntity.ok(notifications);

        } catch (NullPointerException e) {
            log.info("새 알림이 없습니다.");
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NO_CONTENT);

        } catch (Exception e) {
            log.error("알림 조회 실패: {}", e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_GATEWAY);
        }
    }


}
