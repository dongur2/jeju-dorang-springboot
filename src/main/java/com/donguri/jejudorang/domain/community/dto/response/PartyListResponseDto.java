package com.donguri.jejudorang.domain.community.dto.response;

import com.donguri.jejudorang.domain.community.entity.BoardType;
import com.donguri.jejudorang.domain.community.entity.Community;
import com.donguri.jejudorang.domain.community.entity.JoinState;
import com.donguri.jejudorang.global.common.DateFormat;

import java.util.List;

public record PartyListResponseDto (
    Long id,
    BoardType type,
    JoinState state,
    String title,
    String createdAt,
    int viewCount,
    List<String> tags,
    int bookmarkCount
) {
    public static PartyListResponseDto from(Community community, List<String> tagList) {
        return new PartyListResponseDto(
                community.getId(),
                community.getType(),
                community.getState(),
                community.getTitle(),
                DateFormat.calculateTime(community.getCreatedAt()),
                community.getViewCount(),
                tagList,
                community.getBookmarksCount()
        );
    }
}