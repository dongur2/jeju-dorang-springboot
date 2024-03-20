package com.donguri.jejudorang.domain.community.dto.request.comment;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
@Schema(description = "대댓글 작성 요청 정보")
public record RecommentRequest(

        @Schema(description = "해당 게시글 ID")
        Long postId,

        @Schema(description = "대댓글의 원댓글 ID")
        Long cmtId,

        @Schema(description = "50자를 초과하지 않는 내용")
        @Size(max = 50, message = "댓글은 50자를 초과할 수 없습니다.")
        @NotBlank(message = "댓글 내용을 입력해주세요.")
        String content

) {}
