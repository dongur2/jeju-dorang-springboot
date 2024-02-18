package com.donguri.jejudorang.domain.community.dto.response;

import com.donguri.jejudorang.domain.community.entity.BoardType;
import com.donguri.jejudorang.domain.community.entity.Community;
import com.donguri.jejudorang.domain.community.entity.JoinState;
import com.donguri.jejudorang.global.common.InvalidState;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.Optional;

@Builder
public record CommunityListResponse(
    Long id,
    BoardType type,
    String state,
    String title,
    String nickname,
    String writerId,
    LocalDateTime createdAt,
    int viewCount,
    int bookmarkCount,
    int commentCount

) {
    public static CommunityListResponse from(Community community) {

        // 모임/잡담글 구분: 잡담글일 경우 NOT_PARTY
        JoinState state = Optional.ofNullable(community.getState()).orElseGet(() -> JoinState.NOT_PARTY);

        // 탈퇴회원 글인지 구분
        return Optional.ofNullable(community.getWriter())
                .map(writer -> {
                    String nickname = writer.getProfile().getNickname();
                    String externalId = writer.getProfile().getExternalId();

                    return convertToDtoFrom(community, state, nickname, externalId);

                })
                .orElseGet(() -> convertToDtoFrom(community, state, InvalidState.INVALID.name(), InvalidState.INVALID.name()));

    }

    private static CommunityListResponse convertToDtoFrom(Community community, JoinState state, String nickname, String writerId) {
        return CommunityListResponse.builder()
                .id(community.getId())
                .type(community.getType())
                .state(state.name())
                .title(community.getTitle())
                .nickname(nickname)
                .writerId(writerId)
                .createdAt(community.getCreatedAt())
                .viewCount(community.getViewCount())
                .bookmarkCount(community.getBookmarkCount())
                .commentCount(community.getCommentCount())
                .build();
    }
}