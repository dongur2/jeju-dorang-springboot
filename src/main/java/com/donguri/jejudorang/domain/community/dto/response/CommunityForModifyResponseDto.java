package com.donguri.jejudorang.domain.community.dto.response;

import com.donguri.jejudorang.domain.community.entity.BoardType;
import com.donguri.jejudorang.domain.community.entity.Community;
import com.donguri.jejudorang.domain.community.entity.JoinState;

import java.util.List;

public record CommunityForModifyResponseDto (
    Long id,
    BoardType type,
    JoinState state,
    String title,
    String content,
    List<String> tags
) {
    public static CommunityForModifyResponseDto from(Community community, List<String> tagList) {
        return new CommunityForModifyResponseDto(
                community.getId(),
                community.getType(),
                community.getState(),
                community.getTitle(),
                community.getContent(),
                tagList
        );
    }
}

