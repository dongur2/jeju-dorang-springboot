package com.donguri.jejudorang.domain.community.dto.request;


import com.donguri.jejudorang.domain.community.entity.Community;



public record CommunityWriteRequestDto (
    String title,
    String type,
    String content,
    String tags
) {
    public Community toEntity() {
        Community newEntity = Community.builder()
                .title(title)
                .content(content)
                .build();
        newEntity.setBoardType(type);
        newEntity.setDefaultJoinState();

        return newEntity;
    }
}
