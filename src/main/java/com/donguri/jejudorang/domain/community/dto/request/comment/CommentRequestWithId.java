package com.donguri.jejudorang.domain.community.dto.request.comment;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
@Schema(description = "댓글 수정을 위한 요청 정보")
public record CommentRequestWithId (

        Long cmtId,

        @Size(max = 50, message = "댓글은 50자를 초과할 수 없습니다.")
        @NotBlank(message = "댓글 내용을 입력해주세요.")
        String content
)
{}
