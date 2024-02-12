package com.donguri.jejudorang.domain.community.dto.response;

import com.donguri.jejudorang.domain.community.entity.BoardType;
import com.donguri.jejudorang.domain.community.entity.Community;
import com.donguri.jejudorang.global.common.DateFormat;
import lombok.Builder;

import java.util.List;

@Builder
public record ChatListResponse(
        Long id,
        BoardType type,
        String title,
        String nickname,
        String createdAt,
        int viewCount,
        List<String> tags,
        int bookmarkCount,
        int commentCount
) {
    public static ChatListResponse from(Community community, List<String> tagList) {
        String nickname = null;
        if (community.getWriter() != null) {
            nickname = community.getWriter().getProfile().getNickname();
        }

        return ChatListResponse.builder()
                .id(community.getId())
                .type(community.getType())
                .title(community.getTitle())
                .nickname(nickname)
                .createdAt(DateFormat.calculateTime(community.getCreatedAt()))
                .viewCount(community.getViewCount())
                .tags(tagList)
                .bookmarkCount(community.getBookmarkCount())
                .commentCount(community.getCommentCount())
                .build();

    }
}