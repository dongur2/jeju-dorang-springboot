package com.donguri.jejudorang.domain.board.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BoardUpdateRequestDto {
    private Long boardId;
    private String title;
    private String tags;
    private String type;
    private String content;
}
