package com.donguri.jejudorang.domain.notification.repository;


import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Optional;

public interface NotificationRepository {

    SseEmitter save(Long userId, SseEmitter sseEmitter);

    void delete(Long userId);

    Optional<SseEmitter> get(Long userId);

    void flush();


}
