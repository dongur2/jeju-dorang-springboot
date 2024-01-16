package com.donguri.jejudorang.domain.community.dto.response;

import com.donguri.jejudorang.domain.community.entity.BoardType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class ChatDetailResponseDto {
    private final Long id;
    private final BoardType type;
    private final String title;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final int viewCount;
    private final String content;
    private final List<String> tags;
    private final int bookmarkCount;

    @Builder
    public ChatDetailResponseDto(Long id, BoardType type, String title, LocalDateTime createdAt, LocalDateTime updatedAt, int viewCount, String content, List<String> tags, int bookmarkCount) {
        this.id = id;
        this.type = type;
        this.title = title;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.viewCount = viewCount;
        this.content = content;
        this.tags = tags;
        this.bookmarkCount = bookmarkCount;
    }
}
