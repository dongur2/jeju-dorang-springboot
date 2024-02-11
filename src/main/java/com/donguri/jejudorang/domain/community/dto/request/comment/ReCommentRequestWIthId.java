package com.donguri.jejudorang.domain.community.dto.request.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record ReCommentRequestWIthId(

        Long rCmtId,

        @Size(max = 50, message = "댓글은 50자를 초과할 수 없습니다.")
        @NotBlank(message = "댓글 내용을 입력해주세요.")
        String content

) {}
