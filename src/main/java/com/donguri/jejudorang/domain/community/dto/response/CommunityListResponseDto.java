package com.donguri.jejudorang.domain.community.dto.response;

import com.donguri.jejudorang.domain.community.entity.BoardType;
import com.donguri.jejudorang.domain.community.entity.Community;
import com.donguri.jejudorang.domain.community.entity.JoinState;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record CommunityListResponseDto(
    Long id,
    BoardType type,
    JoinState state,
    String title,
    LocalDateTime createdAt,
    int viewCount,
    int bookmarkCount
) {
    public static CommunityListResponseDto from(Community community, List<String> tagList) {
        JoinState state = null;
        if (community.getState() != null) {
            state = community.getState();
        }

        return CommunityListResponseDto.builder()
                .id(community.getId())
                .type(community.getType())
                .state(state)
                .title(community.getTitle())
                .createdAt(community.getCreatedAt())
                .viewCount(community.getViewCount())
                .bookmarkCount(community.getBookmarksCount())
                .build();

    }
}