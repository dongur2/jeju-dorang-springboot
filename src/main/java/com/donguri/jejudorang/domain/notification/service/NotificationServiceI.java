package com.donguri.jejudorang.domain.notification.service;

import com.donguri.jejudorang.domain.notification.repository.NotificationRepository;
import com.donguri.jejudorang.global.auth.jwt.JwtProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

@Slf4j
@Service
public class NotificationServiceI implements NotificationService{

    private final Long DEFAULT_TIMEOUT = 60 * 1000 * 60L; // 60M
    private final String NOTIFICATION_NAME = "notification";

    @Autowired private final JwtProvider jwtProvider;
    @Autowired private final NotificationRepository notificationRepository;

    public NotificationServiceI(JwtProvider jwtProvider, NotificationRepository notificationRepository) {
        this.jwtProvider = jwtProvider;
        this.notificationRepository = notificationRepository;
    }


    @Override
    @Transactional
    public SseEmitter connectNotification(String accessToken) throws IOException {
        try {
            Long userId = jwtProvider.getIdFromJwtToken(accessToken);

            // 현재 로그인 유저 ID & SseEmitter 인스턴스 생성해 저장
            SseEmitter sseEmitter = notificationRepository.save(userId, new SseEmitter(DEFAULT_TIMEOUT));
            log.info("CONNECT :: SseEmitter: {}", sseEmitter);

            // SseEmitter 인스턴스 제거할 상황
            sseEmitter.onCompletion(() -> notificationRepository.delete(userId)); // 요청 완료시 호출될 코드 (콜백)
            sseEmitter.onTimeout(() -> notificationRepository.delete(userId)); // 요청 시간 초과시 호출될 코드
            sseEmitter.onError((e) -> notificationRepository.delete(userId)); // 요청중 에러 발생시 호출될 코드

            // 처음 연결시 전달할 더미 데이터 생성: SseEmitter생성 후 아무런 데이터도 보내지 않으면 503 에러 발생
            sseEmitter.send(SseEmitter.event()
                    .name(NOTIFICATION_NAME) // 이벤트 이름 -> 클라이언트에서 전달받을 데이터 이름
                    .data("Connected")); // 더미 데이터

            return sseEmitter;

        } catch (Exception e) {
            log.error("SseEmitter 연결에 실패했습니다 : {}", e.getMessage());
            throw e;
        }
    }

}
