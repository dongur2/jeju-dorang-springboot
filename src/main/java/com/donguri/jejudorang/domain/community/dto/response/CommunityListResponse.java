package com.donguri.jejudorang.domain.community.dto.response;

import com.donguri.jejudorang.domain.community.entity.Community;
import com.donguri.jejudorang.domain.community.entity.JoinState;
import com.donguri.jejudorang.global.common.DateFormat;
import com.donguri.jejudorang.global.common.InvalidState;
import lombok.Builder;

import java.util.List;
import java.util.Optional;

@Builder
public record CommunityListResponse(
    Long id,
    String type,
    String state,
    String title,

    String nickname,

    String createdAt,
    List<String> tags,
    int viewCount,
    int bookmarkCount,
    int commentCount

) {
    public static CommunityListResponse from(Community community, List<String> tagList) {

        // 모임/잡담글 구분: 잡담글일 경우 NOT_PARTY
        JoinState state = Optional.ofNullable(community.getState()).orElseGet(() -> JoinState.NOT_PARTY);

        return Optional.ofNullable(community.getWriter())
                .map(writer -> convertToDtoFrom(state, community, tagList, writer.getProfile().getNickname()))
                .orElseGet(() -> convertToDtoFrom(state, community, tagList, InvalidState.INVALID.name()));

    }

    private static CommunityListResponse convertToDtoFrom(JoinState state, Community community, List<String> tagList, String writerNickname) {
        return CommunityListResponse.builder()
                .id(community.getId())
                .type(community.getType().name())
                .state(state.name())
                .title(community.getTitle())
                .nickname(writerNickname)
                .createdAt(DateFormat.calculateTime(community.getCreatedAt()))
                .viewCount(community.getViewCount())
                .tags(tagList)
                .bookmarkCount(community.getBookmarkCount())
                .commentCount(community.getCommentCount())
                .build();
    }
}