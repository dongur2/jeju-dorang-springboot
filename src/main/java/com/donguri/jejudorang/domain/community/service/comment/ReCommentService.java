package com.donguri.jejudorang.domain.community.service.comment;

import com.donguri.jejudorang.domain.community.dto.request.comment.ReCommentRequest;

public interface ReCommentService {

    // 새 대댓글 작성
    void writeNewReComment(String accessToken, ReCommentRequest newReComment);

}
