package com.donguri.jejudorang.domain.notification.service;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

public interface NotificationService {

    SseEmitter connectNotification(String accessToken) throws IOException;

    void sendNotification(Long postWriterId, String postTitle, Long notificationId);

}