package com.donguri.jejudorang.domain.community.dto.response.comment;

import com.donguri.jejudorang.domain.community.entity.comment.Comment;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record CommentResponse (
        Long cmtId,
        String pic,
        String nickname,
        String writerId,
        String content,
        LocalDateTime createdAt,

        int depth
//        List<ReCommentResponse> recomments
){

    public static CommentResponse from(Comment cmt) {
        return CommentResponse.builder()
                .cmtId(cmt.getId())
                .pic(cmt.getUser().getProfile().getImgUrl())
                .nickname(cmt.getUser().getProfile().getNickname())
                .writerId(cmt.getUser().getProfile().getExternalId())
                .content(cmt.getContent())
                .createdAt(cmt.getCreatedAt())
                .depth(cmt.getCmtDepth())
                .build();
    }

}
