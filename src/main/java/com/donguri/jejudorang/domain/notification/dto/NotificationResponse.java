package com.donguri.jejudorang.domain.notification.dto;

import com.donguri.jejudorang.domain.community.entity.BoardType;
import com.donguri.jejudorang.domain.notification.entity.Notification;
import com.donguri.jejudorang.domain.notification.entity.NotifyType;
import com.donguri.jejudorang.global.common.DateFormat;
import lombok.Builder;
import org.yaml.snakeyaml.comments.CommentType;

@Builder
public record NotificationResponse(
        Long notifyId,
        Long userId,
        String content,
        String type,
        Long postId,
        String isChecked,
        String createdAt,
        String cmtType
) {
    public static NotificationResponse from(Notification notification) {
        String type = "parties";
        if(notification.getPost().getType().equals(BoardType.CHAT)) {
            type = "chats";
        }

        String cmtType = "댓글";
        if(notification.getType().equals(NotifyType.RECOMMENT)) {
            cmtType = "대댓글";
        }

        return NotificationResponse.builder()
                .notifyId(notification.getId())
                .userId(notification.getOwner().getId())
                .content(notification.getContent())
                .type(type)
                .postId(notification.getPost().getId())
                .isChecked(notification.getIsChecked().name())
                .createdAt(DateFormat.calculateTime(notification.getCreatedAt()))
                .cmtType(cmtType)
                .build();
    }
}
