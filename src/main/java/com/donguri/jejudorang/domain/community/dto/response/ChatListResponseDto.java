package com.donguri.jejudorang.domain.community.dto.response;

import com.donguri.jejudorang.domain.community.entity.BoardType;
import com.donguri.jejudorang.domain.community.entity.Community;
import com.donguri.jejudorang.global.common.DateFormat;

import java.util.List;

public record ChatListResponseDto (
        Long id,
        BoardType type,
        String title,
        String createdAt,
        int viewCount,
        List<String> tags,
        int bookmarkCount
) {
    public static ChatListResponseDto from(Community community) {
        return new ChatListResponseDto(
                community.getId(),
                community.getType(),
                community.getTitle(),
                DateFormat.calculateTime(community.getCreatedAt()),
                community.getViewCount(),
                community.getTags(),
                community.getBookmarksCount()
        );
    }
}