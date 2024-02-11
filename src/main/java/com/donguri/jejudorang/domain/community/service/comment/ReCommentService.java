package com.donguri.jejudorang.domain.community.service.comment;

import com.donguri.jejudorang.domain.community.dto.request.comment.ReCommentRequest;
import com.donguri.jejudorang.domain.community.dto.request.comment.ReCommentRequestWIthId;

public interface ReCommentService {

    // 새 대댓글 작성
    void writeNewReComment(String accessToken, ReCommentRequest newReComment);

    // 대댓글 수정
    void modifyReComment(String accessToken, ReCommentRequestWIthId reCommentToUpdate) throws IllegalAccessException;

    // 대댓글 삭제
    void deleteReComment(String accessToken, Long rCmtId) throws IllegalAccessException;
}
