package com.donguri.jejudorang.domain.notification.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Repository
public class SseEmitterRepositoryI implements SseEmitterRepository {

    private Map<Long, SseEmitter> emitterMap = new ConcurrentHashMap<>();

    @Override
    public SseEmitter save(Long userId, SseEmitter sseEmitter) {
        emitterMap.put(userId, sseEmitter);
        log.info("SseEmitter가 NotificationRepository에 저장되었습니다. 키: {}, 값: {}", userId, sseEmitter);
        return sseEmitter;
    }

    @Override
    public void delete(Long userId) {
        emitterMap.remove(userId);
        log.info("SseEmitter가 NotificationRepository에서 삭제되었습니다.");
    }

    @Override
    public Optional<SseEmitter> get(Long userId) {
        return Optional.ofNullable(emitterMap.get(userId));
    }

    @Override
    public void flush() {
        emitterMap.keySet().forEach(key -> emitterMap.remove(key));
    }


}
