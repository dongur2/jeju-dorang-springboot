package com.donguri.jejudorang.domain.community.dto.response.comment;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ReCommentResponse(
        Long cmtId,
        Long reCmtId,
        String pic,
        String nickname,
        String writerId,
        String content,
        LocalDateTime createdAt
) {
}
