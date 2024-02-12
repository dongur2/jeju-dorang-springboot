package com.donguri.jejudorang.domain.community.dto.response;

import com.donguri.jejudorang.domain.community.entity.Community;
import com.donguri.jejudorang.domain.community.entity.JoinState;
import lombok.Builder;

import java.util.List;

@Builder
public record CommunityForModifyResponse(
    Long id,
    String type,
    JoinState state,
    String title,
    String content,
    List<String> tags
) {
    public static CommunityForModifyResponse from(Community community, List<String> tagList) {
        return CommunityForModifyResponse.builder()
                .id(community.getId())
                .type(community.getType().name().toLowerCase())
                .state(community.getState())
                .title(community.getTitle())
                .content(community.getContent())
                .tags(tagList)
                .build();
    }
}

