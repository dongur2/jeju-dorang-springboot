package com.donguri.jejudorang.domain.community.dto.response.comment;

import com.donguri.jejudorang.domain.community.entity.comment.Comment;
import com.donguri.jejudorang.domain.community.entity.comment.IsDeleted;
import com.donguri.jejudorang.global.common.InvalidState;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;

@Builder
public record CommentResponse (
        Long cmtId,
        String pic,
        String nickname,
        String writerId,
        String content,
        LocalDateTime createdAt,
        int depth
){

    public static CommentResponse from(Comment cmt) {
        // 삭제처리된 댓글일 경우
        if(cmt.getIsDeleted() == IsDeleted.DELETED) {
            return CommentResponse.builder()
                    .cmtId(cmt.getId())
                    .pic(InvalidState.INVALID.toString())
                    .nickname(InvalidState.INVALID.toString())
                    .writerId(InvalidState.INVALID.toString())
                    .content(InvalidState.INVALID.toString())
                    .createdAt(cmt.getCreatedAt())
                    .depth(cmt.getCmtDepth())
                    .build();
        }

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
