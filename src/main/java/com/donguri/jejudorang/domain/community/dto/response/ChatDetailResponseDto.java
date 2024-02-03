package com.donguri.jejudorang.domain.community.dto.response;

import com.donguri.jejudorang.domain.community.entity.BoardType;
import com.donguri.jejudorang.domain.community.entity.Community;

import java.time.LocalDateTime;
import java.util.List;

public record ChatDetailResponseDto (
        Long id,
        BoardType type,
        String title,
        String nickname,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        int viewCount,
        String content,
        List<String> tags,
        int bookmarkCount
) {
    public static ChatDetailResponseDto from(Community community, List<String> tagList) {
        return new ChatDetailResponseDto(
                community.getId(),
                community.getType(),
                community.getTitle(),
                community.getWriter().getProfile().getNickname(),
                community.getCreatedAt(),
                community.getUpdatedAt(),
                community.getViewCount(),
                community.getContent(),
                tagList,
                community.getBookmarksCount()
        );
    }
}
