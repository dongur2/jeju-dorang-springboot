package com.donguri.jejudorang.domain.notification.service;

import com.donguri.jejudorang.domain.user.entity.User;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

public interface NotificationService {

    SseEmitter connectNotification(String accessToken) throws IOException;

    void sendNotification(User postWriter, String postTitle, Long notificationId);

}
