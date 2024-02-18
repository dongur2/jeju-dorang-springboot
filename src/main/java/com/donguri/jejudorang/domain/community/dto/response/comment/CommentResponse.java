package com.donguri.jejudorang.domain.community.dto.response.comment;

import com.donguri.jejudorang.domain.community.entity.comment.Comment;
import com.donguri.jejudorang.domain.community.entity.comment.IsDeleted;
import com.donguri.jejudorang.global.common.InvalidState;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Optional;


@Builder
public record CommentResponse (
        Long cmtId,
        String pic,
        String nickname,
        String writerId,
        String content,
        LocalDateTime createdAt,
        int depth
) {

    public static CommentResponse from(Comment cmt) {

        // 탈퇴한 회원의 댓글일 경우를 구분: pic, nickname, writerId
        return Optional.ofNullable(cmt.getUser())
                // 탈퇴하지 않은 경우 삭제한 댓글인지 구분
                .map(writer -> {
                    String img = writer.getProfile().getImgUrl();
                    String nickname = writer.getProfile().getNickname();
                    String externalId = writer.getProfile().getExternalId();
                    String content = cmt.getContent();

                    if(cmt.getIsDeleted() == IsDeleted.DELETED) {
                        img = IsDeleted.DELETED.name();
                        nickname = IsDeleted.DELETED.name();
                        externalId = IsDeleted.DELETED.name();
                        content = IsDeleted.DELETED.name();
                    }

                    return convertToDtoFrom(cmt, img, nickname, externalId, content);

                })
                .orElseGet(() -> convertToDtoFrom(cmt, InvalidState.INVALID.name(), InvalidState.INVALID.name(), InvalidState.INVALID.name(), cmt.getContent()));

    }

    private static CommentResponse convertToDtoFrom(Comment cmt, String writerProfPic, String writerNickname, String writerExternalId, String content) {
        return CommentResponse.builder()
                .cmtId(cmt.getId())
                .pic(writerProfPic)
                .nickname(writerNickname)
                .writerId(writerExternalId)
                .content(content)
                .createdAt(cmt.getCreatedAt())
                .depth(cmt.getCmtDepth())
                .build();
    }

}
