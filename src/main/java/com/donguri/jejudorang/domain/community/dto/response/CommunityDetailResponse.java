package com.donguri.jejudorang.domain.community.dto.response;

import com.donguri.jejudorang.domain.bookmark.entity.CommunityBookmark;
import com.donguri.jejudorang.domain.community.entity.BoardType;
import com.donguri.jejudorang.domain.community.entity.Community;
import com.donguri.jejudorang.domain.community.entity.JoinState;
import com.donguri.jejudorang.global.common.InvalidState;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Getter
@Builder
public class CommunityDetailResponse {
    private Long id;
    private BoardType type;
    private JoinState state;
    private String title;
    private String content;

    private String nickname;
    private String writerId;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private int viewCount;
    private int commentCount;
    private int bookmarkCount;

    private List<String> tags;

    private boolean isBookmarked;


    /*
    * 상세글 조회: 비회원일 경우 nowViewer == null
    * */
    public static CommunityDetailResponse from(Community community, List<String> tagList, String nowViewer) {

        // 탈퇴한 회원의 글일 경우: community.writer == null
        return Optional.ofNullable(community.getWriter())
                .map(writer -> convertToDtoFrom(community, tagList, nowViewer, writer.getProfile().getNickname(), writer.getProfile().getExternalId()))
                .orElseGet(() -> convertToDtoFrom(community, tagList, nowViewer, InvalidState.INVALID.name(), InvalidState.INVALID.name()));

    }

    private static CommunityDetailResponse convertToDtoFrom(Community community, List<String> tagList, String nowViewer, String writerNickname, String writerExternalId) {
        CommunityDetailResponse dto = CommunityDetailResponse.builder()
                .id(community.getId())
                .type(community.getType())
                .state(community.getState())
                .title(community.getTitle())
                .nickname(writerNickname)
                .writerId(writerExternalId)
                .content(community.getContent())
                .createdAt(community.getCreatedAt())
                .updatedAt(community.getUpdatedAt())
                .viewCount(community.getViewCount())
                .tags(tagList)
                .bookmarkCount(community.getBookmarkCount())
                .commentCount(community.getCommentCount())
                .build();

        /* 회원이 조회했을 경우 북마크 여부 확인
        * */
        Optional.ofNullable(nowViewer)
                .ifPresent(viewer -> dto.checkAndSetIsBookmarked(community.getBookmarks(), viewer));

        return dto;
    }


    // 북마크 여부 확인
    private void checkAndSetIsBookmarked(Set<CommunityBookmark> bookmarks, String nowViewer) {
        this.setIsBookmarked(bookmarks.stream()
                .anyMatch(bookmark -> bookmark.getUser().getProfile().getExternalId().equals(nowViewer)));

    }

    // isBookmarked 업데이트
    private void setIsBookmarked(boolean checkResult) {
        this.isBookmarked = checkResult;
    }


}
