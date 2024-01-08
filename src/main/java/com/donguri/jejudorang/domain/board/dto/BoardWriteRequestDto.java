package com.donguri.jejudorang.domain.board.dto;

import com.donguri.jejudorang.domain.board.entity.Board;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
public class BoardWriteRequestDto {
    private String title;
    private String tags;
    private String type;
    private String content;

    @Builder
    public BoardWriteRequestDto(String title, String tags, String type, String content) {
        this.title = title;
        this.tags = tags;
        this.type = type;
        this.content = content;
    }

    public Board toEntity() {
        return Board.builder()
                .title(title)
                .tags(tags)
                .type(type)
                .content(content)
                .build();
    }

    @Override
    public String toString() {
        return "BoardWriteRequestDto{" +
                "title='" + title + '\'' +
                ", tags='" + tags + '\'' +
                ", type='" + type + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}
