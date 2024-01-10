package com.donguri.jejudorang.domain.board.dto.request;

import com.donguri.jejudorang.domain.board.entity.Board;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BoardWriteRequestDto {
    private String title;
    private String tags;
    private String type;
    private String content;

    public Board toEntity() {
        return Board.builder()
                .title(title)
                .tags(tags)
                .type(type)
                .content(content)
                .build();
    }
}
