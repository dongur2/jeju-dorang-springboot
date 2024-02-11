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
    String nickname,
    String writerId,
    LocalDateTime createdAt,
    int viewCount,
    int bookmarkCount,
    int commentCount
) {
    public static CommunityListResponseDto from(Community community) {
        JoinState state = null;
        if (community.getState() != null) {
            state = community.getState();
        }

        String writerId = null;
        String nickname = null;
        if (community.getWriter() != null) {
            writerId = community.getWriter().getProfile().getExternalId();
            nickname = community.getWriter().getProfile().getNickname();
        }


        return CommunityListResponseDto.builder()
                .id(community.getId())
                .type(community.getType())
                .state(state)
                .title(community.getTitle())
                .nickname(nickname)
                .writerId(writerId)
                .createdAt(community.getCreatedAt())
                .viewCount(community.getViewCount())
                .bookmarkCount(community.getBookmarksCount())
                .commentCount(community.getCommentsCount()+community.getRecommentsCount())
                .build();

    }
}