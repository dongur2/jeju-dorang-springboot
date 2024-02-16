package com.donguri.jejudorang.domain.notification.service;

import com.donguri.jejudorang.domain.community.entity.Community;
import com.donguri.jejudorang.domain.notification.dto.NotificationResponse;
import com.donguri.jejudorang.domain.user.entity.User;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;

public interface NotificationService {

    SseEmitter connectNotification(String accessToken) throws IOException;

    void sendNotification(User postWriter, Community post, Long notificationId);

    List<NotificationResponse> getNotifications(String accessToken);

    // 알림 읽음 처리 후 상세글 요청 url 리턴
    String updateNotificationToChecked(String accessToken, Long notificationId);
}
