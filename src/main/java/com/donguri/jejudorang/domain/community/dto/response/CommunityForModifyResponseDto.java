package com.donguri.jejudorang.domain.community.dto.response;

import com.donguri.jejudorang.domain.community.entity.BoardType;
import com.donguri.jejudorang.domain.community.entity.JoinState;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class CommunityForModifyResponseDto {
    private final Long id;
    private final BoardType type;
    private final JoinState state;
    private final String title;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final int viewCount;
    private final String content;
    private final List<String> tags;
    private final int likedCount;

    @Builder
    public CommunityForModifyResponseDto(Long id, BoardType type, JoinState state, String title, LocalDateTime createdAt, LocalDateTime updatedAt, int viewCount, String content, List<String> tags, int likedCount) {
        this.id = id;
        this.type = type;
        this.state = state;
        this.title = title;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.viewCount = viewCount;
        this.content = content;
        this.tags = tags;
        this.likedCount = likedCount;
    }
}
