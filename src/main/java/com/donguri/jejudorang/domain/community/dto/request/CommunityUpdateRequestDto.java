package com.donguri.jejudorang.domain.community.dto.request;


public record CommunityUpdateRequestDto (
        Long communityId,
        String title,
        String tags,
        String type,
        String content
) {

}
