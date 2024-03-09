package com.donguri.jejudorang.domain.notification.service;

import com.donguri.jejudorang.domain.community.entity.BoardType;
import com.donguri.jejudorang.domain.community.entity.Community;
import com.donguri.jejudorang.domain.notification.dto.NotificationResponse;
import com.donguri.jejudorang.domain.notification.entity.IsChecked;
import com.donguri.jejudorang.domain.notification.entity.Notification;
import com.donguri.jejudorang.domain.notification.entity.NotifyType;
import com.donguri.jejudorang.domain.notification.repository.NotificationRepository;
import com.donguri.jejudorang.domain.notification.repository.SseEmitterRepository;
import com.donguri.jejudorang.domain.user.entity.User;
import com.donguri.jejudorang.global.auth.jwt.JwtProvider;
import com.donguri.jejudorang.global.error.CustomErrorCode;
import com.donguri.jejudorang.global.error.CustomException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Comparator;
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
    public void sendNotification(User postWriter, Community post, Long notificationId, NotifyType type) {

        StringBuilder notifyData = new StringBuilder();

        if(type.equals(NotifyType.COMMENT)) {
            notifyData.append("[").append(post.getTitle()).append("] 글에 새 댓글이 달렸습니다.");
        } else {
            notifyData.append("[").append(post.getTitle()).append("] 글의 댓글에 새 대댓글이 달렸습니다.");
        }

        sseEmitterRepository.get(postWriter.getId()).ifPresentOrElse(sseEmitter -> {

            try {
                sseEmitter.send(SseEmitter.event()
                        .id(notificationId.toString())
                        .name(NOTIFICATION_NAME)
                        .data(notifyData.toString()));

                log.info("실시간 알림 전송 완료");

            } catch (IOException e) {
                log.error("새로운 알림 전송에 실패했습니다: {}", e.getMessage());
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "새로운 알림 전송에 실패했습니다.");
            }

        }, () -> log.info("sseEmitter를 찾을 수 없습니다. (현재 로그인한 회원이 아닙니다)"));

        // 상대방의 접속 여부와 상관없이 알림 저장
        saveNotification(postWriter, post, type, notifyData.toString());

    }

    @Override
    public void saveNotification(User postWriter, Community post, NotifyType type, String notifyData) {
        try {
            Notification save = notificationRepository.save(Notification.builder()
                    .owner(postWriter)
                    .content(notifyData)
                    .post(post)
                    .isChecked(IsChecked.NOT_YET)
                    .type(type)
                    .build()
            );
            log.info("알림 DB에 저장 완료: {} - {}", save.getType().name(), save.getContent());

        } catch (Exception e) {
            log.error("알림 저장 실패: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    @Transactional
    public List<NotificationResponse> getNotifications(String accessToken) {
        Long idFromJwtToken = jwtProvider.getIdFromJwtToken(accessToken);

        return notificationRepository.findAllByOwnerId(idFromJwtToken)
                .map(notifications -> notifications.stream()
                        .sorted(Comparator.comparing(Notification::getCreatedAt).reversed())
                        .map(NotificationResponse::from).toList())
                .orElseThrow(() -> new CustomException(CustomErrorCode.NO_NOTIFICATION));
    }

    @Override
    @Transactional
    public String updateNotificationToChecked(String accessToken, Long notificationId) {
        Long idFromJwtToken = jwtProvider.getIdFromJwtToken(accessToken);

        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new CustomException(CustomErrorCode.NOTIFICATION_NOT_FOUND));
        try {
            if (!Objects.equals(idFromJwtToken, notification.getOwner().getId())) {
                throw new CustomException(CustomErrorCode.PERMISSION_ERROR);
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
            throw e;
        }
    }

    @Override
    @Transactional
    public void deleteNotification(String accessToken, Long notificationId) {
        Long idFromJwtToken = jwtProvider.getIdFromJwtToken(accessToken);

        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new CustomException(CustomErrorCode.NOTIFICATION_NOT_FOUND));

        try {
            if (!Objects.equals(idFromJwtToken, notification.getOwner().getId())) {
                throw new CustomException(CustomErrorCode.PERMISSION_ERROR);
            }

            notificationRepository.delete(notification);

        } catch (Exception e) {
          throw e;
        }
    }

    @Override
    @Transactional
    public void findAndDeleteAllNotificationsByUserId(Long userId) {
        try {
            notificationRepository.findAllByOwnerId(userId)
                    .orElseThrow(() -> new CustomException(CustomErrorCode.NO_NOTIFICATION));

            notificationRepository.deleteAllByOwnerId(userId);

        } catch (CustomException e) {
            log.info("삭제할 알림이 없습니다.");

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
