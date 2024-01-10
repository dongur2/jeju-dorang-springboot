package com.donguri.jejudorang.domain.board.dto.request;

import com.donguri.jejudorang.domain.board.entity.Board;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class BoardUpdateRequestDto {
    private Long boardId;
    private String title;
    private String tags;
    private String type;
    private String content;

    @Builder
    public BoardUpdateRequestDto(Long boardId, String title, String tags, String type, String content) {
        this.boardId = boardId;
        this.type = type;
        this.title = title;
        this.tags = tags;
        this.content = content;
    }

    public Board toEntity() {
        return Board.builder()
                .type(type)
                .title(title)
                .tags(tags)
                .content(content)
                .build();
    }

    @Override
    public String toString() {
        return "BoardUpdateRequestDto{" +
                "boardId=" + boardId +
                ", title='" + title + '\'' +
                ", tags='" + tags + '\'' +
                ", type='" + type + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}
