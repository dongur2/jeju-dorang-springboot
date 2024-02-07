package com.donguri.jejudorang.domain.community.dto.response;

import com.donguri.jejudorang.domain.community.entity.BoardType;
import com.donguri.jejudorang.domain.community.entity.Community;
import com.donguri.jejudorang.domain.community.entity.JoinState;
import com.donguri.jejudorang.global.common.DateFormat;
import lombok.Builder;

import java.util.List;

@Builder
public record CommunityListResponseDto(
    Long id,
    BoardType type,
    JoinState state,
    String title,
    String nickname,
    String createdAt,
    int viewCount,
    List<String> tags,
    int bookmarkCount
) {
    public static CommunityListResponseDto from(Community community, List<String> tagList) {
        String nickname = null;
        if (community.getWriter() != null) {
            nickname = community.getWriter().getProfile().getNickname();
        }

        JoinState state = null;
        if (community.getState() != null) {
            state = community.getState();
        }

        return CommunityListResponseDto.builder()
                .id(community.getId())
                .type(community.getType())
                .state(state)
                .title(community.getTitle())
                .nickname(nickname)
                .createdAt(DateFormat.calculateTime(community.getCreatedAt()))
                .viewCount(community.getViewCount())
                .tags(tagList)
                .bookmarkCount(community.getBookmarksCount())
                .build();

    }
}