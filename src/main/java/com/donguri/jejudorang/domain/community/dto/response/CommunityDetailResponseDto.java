package com.donguri.jejudorang.domain.community.dto.response;

import com.donguri.jejudorang.domain.community.entity.BoardType;
import com.donguri.jejudorang.domain.community.entity.Community;
import com.donguri.jejudorang.domain.community.entity.JoinState;

import java.time.LocalDateTime;
import java.util.List;

public record CommunityDetailResponseDto(
    Long id,
    BoardType type,
    JoinState state,
    String title,
    String nickname,
    String writerId,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    int viewCount,
    String content,
    List<String> tags,
    int bookmarkCount,

    boolean isBookmarked

) {
    public static CommunityDetailResponseDto from(Community community, List<String> tagList, String nowViewer) {
        return new CommunityDetailResponseDto(
                community.getId(),
                community.getType(),
                community.getState(),
                community.getTitle(),
                community.getWriter().getProfile().getNickname(),
                community.getWriter().getProfile().getExternalId(),
                community.getCreatedAt(),
                community.getUpdatedAt(),
                community.getViewCount(),
                community.getContent(),
                tagList,
                community.getBookmarksCount(),

                // 현재 로그인한 유저의 북마크 여부 확인
                community.getBookmarks().stream()
                        .anyMatch(bookmark -> bookmark.getUser().getProfile().getExternalId().equals(nowViewer))
        );
    }

    public static CommunityDetailResponseDto from(Community community, List<String> tagList) {
        return new CommunityDetailResponseDto(
                community.getId(),
                community.getType(),
                community.getState(),
                community.getTitle(),
                community.getWriter().getProfile().getNickname(),
                community.getWriter().getProfile().getExternalId(),
                community.getCreatedAt(),
                community.getUpdatedAt(),
                community.getViewCount(),
                community.getContent(),
                tagList,
                community.getBookmarksCount(),
                false
        );
    }
}
