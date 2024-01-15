package com.donguri.jejudorang.domain.board.dto.response;

import com.donguri.jejudorang.domain.board.entity.BoardType;
import com.donguri.jejudorang.domain.board.entity.JoinState;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class BoardListResponseDto {
    private final Long id;
    private final BoardType type;
    private final JoinState state;
    private final String title;
    private final String createdAt; // DateFormat으로 변경한 결과 (~초 전)
    private final int viewCount;
    private final List<String> tags;
    private final int likedCount;

    @Builder
    public BoardListResponseDto(Long id, BoardType type, JoinState state, String title, String createdAt, int viewCount, List<String> tags, int likedCount) {
        this.id = id;
        this.type = type;
        this.state = state;
        this.title = title;
        this.createdAt = createdAt;
        this.viewCount = viewCount;
        this.tags = tags;
        this.likedCount = likedCount;
    }
}
