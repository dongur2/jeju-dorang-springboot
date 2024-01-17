package com.donguri.jejudorang.domain.community.dto.request;

import com.donguri.jejudorang.domain.community.entity.Community;

import java.util.Arrays;

public record CommunityUpdateRequestDto (
        Long communityId,
        String title,
        String tags,
        String type,
        String content
) {
    public Community toEntity() {
        return Community.builder()
                .title(title)
                .content(content)
                .build();
    };
}
