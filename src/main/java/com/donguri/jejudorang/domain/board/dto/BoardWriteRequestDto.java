package com.donguri.jejudorang.domain.board.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
public class BoardWriteRequestDto {
    private String title;
    private String tags;
    private String type;

    @Builder
    public BoardWriteRequestDto(String title, String tags, String type) {
        this.title = title;
        this.tags = tags;
        this.type = type;
    }

    @Override
    public String toString() {
        return "BoardWriteResponseDto{" +
                "title='" + title + '\'' +
                ", tags='" + tags + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
