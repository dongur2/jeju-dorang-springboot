package com.donguri.jejudorang.domain.community.dto.response.comment;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record CommentResponse (
        Long cmtId,
        String pic,
        String nickname,
        String content,
        LocalDateTime createdAt
){ }
