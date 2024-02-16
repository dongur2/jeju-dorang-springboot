package com.donguri.jejudorang.domain.notification.service;

import com.donguri.jejudorang.domain.community.entity.BoardType;
import com.donguri.jejudorang.domain.community.entity.Community;
import com.donguri.jejudorang.domain.notification.dto.NotificationResponse;
import com.donguri.jejudorang.domain.notification.entity.IsChecked;
import com.donguri.jejudorang.domain.notification.entity.Notification;
import com.donguri.jejudorang.domain.notification.repository.NotificationRepository;
import com.donguri.jejudorang.domain.notification.repository.SseEmitterRepository;
import com.donguri.jejudorang.domain.user.entity.User;
import com.donguri.jejudorang.global.auth.jwt.JwtProvider;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class NotificationServiceI implements NotificationService{

    private final Long DEFAULT_TIMEOUT = 60 * 1000 * 60L; // 60M
    private final String NOTIFICATION_NAME = "notification";

    @Autowired private final JwtProvider jwtProvider;
    @Autowired private final SseEmitterRepository sseEmitterRepository;
    @Autowired private final NotificationRepository notificationRepository;

    public NotificationServiceI(JwtProvider jwtProvider, SseEmitterRepository sseEmitterRepository, NotificationRepository notificationRepository) {
        this.jwtProvider = jwtProvider;
        this.sseEmitterRepository = sseEmitterRepository;
        this.notificationRepository = notificationRepository;
    }


    @Override
    public SseEmitter connectNotification(String accessToken) throws IOException {
        try {
            Long userId = jwtProvider.getIdFromJwtToken(accessToken);

            // 현재 로그인 유저 ID & SseEmitter 인스턴스 생성해 저장
            SseEmitter sseEmitter = sseEmitterRepository.save(userId, new SseEmitter(DEFAULT_TIMEOUT));
            log.info("CONNECT :: SseEmitter: {}", sseEmitter);

            // SseEmitter 인스턴스 제거할 상황
            sseEmitter.onCompletion(() -> sseEmitterRepository.delete(userId)); // 요청 완료시 호출될 코드 (콜백)
            sseEmitter.onTimeout(() -> sseEmitterRepository.delete(userId)); // 요청 시간 초과시 호출될 코드
            sseEmitter.onError((e) -> sseEmitterRepository.delete(userId)); // 요청중 에러 발생시 호출될 코드

            // 처음 연결시 전달할 더미 데이터 생성: SseEmitter생성 후 아무런 데이터도 보내지 않으면 503 에러 발생
            sseEmitter.send(SseEmitter.event()
                    .id("") // 알림 ID
                    .name(NOTIFICATION_NAME) // 이벤트 이름 -> 클라이언트에서 전달받을 데이터 이름
                    .data("Connected")); // 더미 데이터

            return sseEmitter;

        } catch (Exception e) {
            log.error("SseEmitter 연결에 실패했습니다 : {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public void sendNotification(User postWriter, Community post, Long notificationId) {
        sseEmitterRepository.get(postWriter.getId()).ifPresentOrElse(sseEmitter -> {
            try {
                String notifyData = "[" + post.getTitle() + "]" + " 글에 새 댓글이 달렸습니다.";

                sseEmitter.send(SseEmitter.event()
                        .id(notificationId.toString())
                        .name(NOTIFICATION_NAME)
                        .data(notifyData));

                log.info("실시간 알림 전송 완료");

                // 알림 저장
                notificationRepository.save(Notification.builder()
                        .owner(postWriter)
                        .content(notifyData)
                        .post(post)
                        .isChecked(IsChecked.NOT_YET)
                        .build()
                );

                log.info("알림 DB에 저장 완료");


            } catch (IOException e) {
                log.error("새로운 알림 전송에 실패했습니다: {}", e.getMessage());
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "새로운 알림 전송에 실패했습니다.");
            }
        }, () -> log.info("sseEmitter를 찾을 수 없습니다. (현재 로그인한 회원이 아닙니다)"));
    }

    @Override
    @Transactional
    public List<NotificationResponse> getNotifications(String accessToken) {
        Long idFromJwtToken = jwtProvider.getIdFromJwtToken(accessToken);

        return notificationRepository.findAllByOwnerId(idFromJwtToken)
                .map(notifications -> notifications.stream().map(NotificationResponse::from).toList())
                .orElseThrow(() -> new NullPointerException("새 알림이 없습니다"));
    }

    @Override
    @Transactional
    public String updateNotificationToChecked(String accessToken, Long notificationId) {
        Long idFromJwtToken = jwtProvider.getIdFromJwtToken(accessToken);

        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new EntityNotFoundException("알림이 존재하지 않습니다."));

        try {
            if (!Objects.equals(idFromJwtToken, notification.getOwner().getId())) {
                throw new Exception("알림은 당사자만 읽음 처리 가능합니다.");
            }

            // 안읽은 알림일 경우에만 읽음 처리 메서드 호출
             if (notification.getIsChecked().equals(IsChecked.NOT_YET)) {
                 notification.updateIsChecked();
             }

            Community post = notification.getPost();
            String type = convertUrlTypeFromCommunity(post.getType());

            // 상세글 컨트롤러 url 리턴
            return "/community/boards/" + type + "/" + post.getId();
            
        } catch (Exception e) {
            log.error("잘못된 접근: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, e.getMessage());
        }
    }

    @Override
    @Transactional
    public void deleteNotification(String accessToken, Long notificationId) throws Exception {
        Long idFromJwtToken = jwtProvider.getIdFromJwtToken(accessToken);

        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new EntityNotFoundException("알림이 존재하지 않습니다."));

        try {
            if (!Objects.equals(idFromJwtToken, notification.getOwner().getId())) {
                throw new Exception("알림은 당사자만 삭제 가능합니다.");
            }

            notificationRepository.delete(notification);

        } catch (Exception e) {
          log.error("알림 삭제 실패: {}", e.getMessage());
          throw e;
        }
    }

    private static String convertUrlTypeFromCommunity(BoardType postType) {
        String type = "chats";
        if (postType.equals(BoardType.PARTY)) {
            type = "parties";
        }
        return type;
    }

}
