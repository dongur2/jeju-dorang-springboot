package com.donguri.jejudorang.domain.community.dto.response;

import com.donguri.jejudorang.domain.community.entity.Community;
import com.donguri.jejudorang.domain.community.entity.JoinState;
import com.donguri.jejudorang.domain.community.entity.comment.IsDeleted;
import com.donguri.jejudorang.global.common.InvalidState;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.Optional;

@Builder
public record CommunityBookmarkListResponse(
    Long id,
    String type,
    String state,
    String title,
    String nickname,
    String writerId,
    LocalDateTime createdAt,
    int viewCount,
    int bookmarkCount,
    int commentCount,
    Long bookmarkId

) {
    public static CommunityBookmarkListResponse from(Long bookmarkId, Community community) {
        if(community == null) {
            return convertToDtoWhenCommunityDeleted(bookmarkId);
        }

        // 모임/잡담글 구분: 잡담글일 경우 NOT_PARTY
        JoinState state = Optional.ofNullable(community.getState()).orElseGet(() -> JoinState.NOT_PARTY);

        // 탈퇴회원 글인지 구분
        return Optional.ofNullable(community.getWriter())
                .map(writer -> {
                    String nickname = writer.getProfile().getNickname();
                    String externalId = writer.getProfile().getExternalId();

                    return convertToBookmarkDtoFrom(community, state.name(), nickname, externalId, bookmarkId);

                })
                .orElseGet(() -> convertToBookmarkDtoFrom(community, state.name(), InvalidState.INVALID.name(), InvalidState.INVALID.name(), bookmarkId));

    }

    public static CommunityBookmarkListResponse from(Community community) {

        // 모임/잡담글 구분: 잡담글일 경우 NOT_PARTY
        JoinState state = Optional.ofNullable(community.getState()).orElseGet(() -> JoinState.NOT_PARTY);

        return convertToWritingDtoFrom(community, state.name());
    }


    private static CommunityBookmarkListResponse convertToWritingDtoFrom(Community community, String state) {
        return CommunityBookmarkListResponse.builder()
                .id(community.getId())
                .type(community.getType().name())
                .state(state)
                .title(community.getTitle())
                .createdAt(community.getCreatedAt())
                .viewCount(community.getViewCount())
                .bookmarkCount(community.getBookmarkCount())
                .commentCount(community.getCommentCount())
                .build();
    }

    private static CommunityBookmarkListResponse convertToBookmarkDtoFrom(Community community, String state, String nickname, String writerId, Long bookmarkId) {
        return CommunityBookmarkListResponse.builder()
                .id(community.getId())
                .type(community.getType().name())
                .state(state)
                .title(community.getTitle())
                .nickname(nickname)
                .writerId(writerId)
                .createdAt(community.getCreatedAt())
                .viewCount(community.getViewCount())
                .bookmarkCount(community.getBookmarkCount())
                .commentCount(community.getCommentCount())
                .bookmarkId(bookmarkId)
                .build();
    }

    private static CommunityBookmarkListResponse convertToDtoWhenCommunityDeleted(Long bookmarkId) {
        return CommunityBookmarkListResponse.builder()
                .type(IsDeleted.DELETED.name())
                .bookmarkId(bookmarkId)
                .build();
    }
}