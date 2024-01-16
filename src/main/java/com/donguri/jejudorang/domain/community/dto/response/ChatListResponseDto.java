package com.donguri.jejudorang.domain.community.dto.response;

import com.donguri.jejudorang.domain.community.entity.BoardType;
import com.donguri.jejudorang.domain.community.entity.JoinState;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class ChatListResponseDto {
    private final Long id;
    private final BoardType type;
    private final String title;
    private final String createdAt; // DateFormat으로 변경한 결과 (~초 전)
    private final int viewCount;
    private final List<String> tags;
    private final int bookmarkCount;

    @Builder
    public ChatListResponseDto(Long id, BoardType type, String title, String createdAt, int viewCount, List<String> tags, int bookmarkCount) {
        this.id = id;
        this.type = type;
        this.title = title;
        this.createdAt = createdAt;
        this.viewCount = viewCount;
        this.tags = tags;
        this.bookmarkCount = bookmarkCount;
    }
}
