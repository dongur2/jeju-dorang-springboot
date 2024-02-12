package com.donguri.jejudorang.domain.community.dto.response;

import com.donguri.jejudorang.domain.community.entity.BoardType;
import com.donguri.jejudorang.domain.community.entity.Community;
import com.donguri.jejudorang.domain.community.entity.JoinState;

import java.util.List;

public record CommunityForModifyResponse(
    Long id,
    BoardType type,
    JoinState state,
    String title,
    String content,
    List<String> tags
) {
    public static CommunityForModifyResponse from(Community community, List<String> tagList) {
        return new CommunityForModifyResponse(
                community.getId(),
                community.getType(),
                community.getState(),
                community.getTitle(),
                community.getContent(),
                tagList
        );
    }
}

