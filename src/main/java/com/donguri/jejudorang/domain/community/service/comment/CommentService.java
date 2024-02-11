package com.donguri.jejudorang.domain.community.service.comment;

import com.donguri.jejudorang.domain.community.dto.request.comment.CommentRequest;
import com.donguri.jejudorang.domain.community.dto.request.comment.CommentRequestWithId;

public interface CommentService {

    // 새 댓글 작성
    void writeNewComment(String accessToken, Long postId, CommentRequest newComment);

    // 댓글 수정
    void modifyComment(String accessToken, CommentRequestWithId commentToUpdate) throws IllegalAccessException;

    // 댓글 삭제
    void deleteComment(String accessToken, Long cmtId) throws IllegalAccessException;
}
