package com.donguri.jejudorang.domain.community.dto.response;

import com.donguri.jejudorang.domain.community.entity.BoardType;
import com.donguri.jejudorang.domain.community.entity.Community;
import com.donguri.jejudorang.domain.community.entity.JoinState;
import com.donguri.jejudorang.global.common.DateFormat;
import jakarta.servlet.http.Part;
import lombok.Builder;

import java.util.List;

@Builder
public record PartyListResponseDto (
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
    public static PartyListResponseDto from(Community community, List<String> tagList) {
        if (community.getWriter() != null) {
            return PartyListResponseDto.builder()
                    .id(community.getId())
                    .type(community.getType())
                    .state(community.getState())
                    .title(community.getTitle())
                    .createdAt(DateFormat.calculateTime(community.getCreatedAt()))
                    .viewCount(community.getViewCount())
                    .tags(tagList)
                    .bookmarkCount(community.getBookmarksCount())
                    .nickname(community.getWriter().getProfile().getNickname())
                    .build();
        } else {
            return PartyListResponseDto.builder()
                    .id(community.getId())
                    .type(community.getType())
                    .state(community.getState())
                    .title(community.getTitle())
                    .createdAt(DateFormat.calculateTime(community.getCreatedAt()))
                    .viewCount(community.getViewCount())
                    .tags(tagList)
                    .bookmarkCount(community.getBookmarksCount())
                    .nickname(null)
                    .build();
        }
    }
}