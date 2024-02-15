package com.donguri.jejudorang.domain.notification.dto;

import com.donguri.jejudorang.domain.notification.entity.Notification;
import lombok.Builder;

@Builder
public record NotificationResponse(
        Long userId,
        String content,
        Long postId,
        String isChecked
) {
    public static NotificationResponse from(Notification notification) {
        return NotificationResponse.builder()
                .userId(notification.getOwner().getId())
                .content(notification.getContent())
                .postId(notification.getPost().getId())
                .isChecked(notification.getIsChecked().name())
                .build();
    }
}
