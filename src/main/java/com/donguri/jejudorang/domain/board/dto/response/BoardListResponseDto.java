package com.donguri.jejudorang.domain.board.dto.response;

import com.donguri.jejudorang.domain.board.entity.BoardType;
import com.donguri.jejudorang.domain.board.entity.JoinState;
import com.donguri.jejudorang.global.common.DateFormat;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class BoardListResponseDto {
    private final Long id;
    private final BoardType type;
    private final JoinState joining;
    private final String title;
    private final String createdAt;
    private final int viewCount;
    private final String content;
    private final List<String> tags;
    private final int likedCount;

    @Builder
    public BoardListResponseDto(Long id, BoardType type, JoinState joining, String title, String createdAt, int viewCount, String content, List<String> tags, int likedCount) {
        this.id = id;
        this.type = type;
        this.joining = joining;
        this.title = title;
        this.createdAt = createdAt;
        this.viewCount = viewCount;
        this.content = content;
        this.tags = tags;
        this.likedCount = likedCount;
    }
}
