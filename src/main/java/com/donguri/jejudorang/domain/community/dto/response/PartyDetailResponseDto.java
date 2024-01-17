package com.donguri.jejudorang.domain.community.dto.response;

import com.donguri.jejudorang.domain.community.entity.BoardType;
import com.donguri.jejudorang.domain.community.entity.Community;
import com.donguri.jejudorang.domain.community.entity.JoinState;

import java.time.LocalDateTime;
import java.util.List;

public record PartyDetailResponseDto (
    Long id,
    BoardType type,
    JoinState state,
    String title,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    int viewCount,
    String content,
    List<String> tags,
    int bookmarkCount
) {
    public static PartyDetailResponseDto from(Community community, List<String> tagList) {
        return new PartyDetailResponseDto(
                community.getId(),
                community.getType(),
                community.getState(),
                community.getTitle(),
                community.getCreatedAt(),
                community.getUpdatedAt(),
                community.getViewCount(),
                community.getContent(),
                tagList,
                community.getBookmarksCount()
        );
    }
}
