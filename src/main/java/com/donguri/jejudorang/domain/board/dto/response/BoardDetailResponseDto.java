package com.donguri.jejudorang.domain.board.dto.response;

import com.donguri.jejudorang.domain.board.entity.BoardType;
import com.donguri.jejudorang.domain.board.entity.JoinState;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class BoardDetailResponseDto {
    private final Long id;
    private final BoardType type;
    private final JoinState joining;
    private final String title;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final int viewCount;
    private final String content;
    private final List<String> tags;

    @Builder
    public BoardDetailResponseDto(Long id, BoardType type, JoinState joining, String title, LocalDateTime createdAt, LocalDateTime updatedAt, int viewCount, String content, List<String> tags) {
        this.id = id;
        this.type = type;
        this.joining = joining;
        this.title = title;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.viewCount = viewCount;
        this.content = content;
        this.tags = tags;
    }
}
