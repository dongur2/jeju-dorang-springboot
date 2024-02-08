package com.donguri.jejudorang.domain.community.dto.response;

import com.donguri.jejudorang.domain.community.entity.BoardType;
import com.donguri.jejudorang.domain.community.entity.Community;
import com.donguri.jejudorang.domain.community.entity.JoinState;
import com.donguri.jejudorang.global.common.DateFormat;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
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
        String nickname = null;
        String writerId = null;
        if (community.getWriter() != null) {
            nickname = community.getWriter().getProfile().getNickname();
            writerId = community.getWriter().getProfile().getExternalId();
        }

        return CommunityDetailResponseDto.builder()
                .id(community.getId())
                .type(community.getType())
                .state(community.getState())
                .title(community.getTitle())
                .nickname(nickname)
                .writerId(writerId)
                .createdAt(community.getCreatedAt())
                .updatedAt(community.getUpdatedAt())
                .viewCount(community.getViewCount())
                .tags(tagList)
                .bookmarkCount(community.getBookmarksCount())

                // 현재 로그인한 유저의 북마크 여부 확인
                .isBookmarked(community.getBookmarks().stream()
                        .anyMatch(bookmark -> bookmark.getUser().getProfile().getExternalId().equals(nowViewer)))
                .build();

    }

    public static CommunityDetailResponseDto from(Community community, List<String> tagList) {
        String nickname = null;
        String writerId = null;
        if (community.getWriter() != null) {
            nickname = community.getWriter().getProfile().getNickname();
            writerId = community.getWriter().getProfile().getExternalId();
        }

        return CommunityDetailResponseDto.builder()
                .id(community.getId())
                .type(community.getType())
                .state(community.getState())
                .title(community.getTitle())
                .nickname(nickname)
                .writerId(writerId)
                .createdAt(community.getCreatedAt())
                .updatedAt(community.getUpdatedAt())
                .viewCount(community.getViewCount())
                .tags(tagList)
                .bookmarkCount(community.getBookmarksCount())
                .build();
    }
    
}
