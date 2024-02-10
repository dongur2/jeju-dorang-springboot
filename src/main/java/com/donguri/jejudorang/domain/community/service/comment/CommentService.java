package com.donguri.jejudorang.domain.community.service.comment;

import com.donguri.jejudorang.domain.community.dto.request.comment.CommentRequest;

public interface CommentService {

    void writeNewComment(String accessToken, Long postId, CommentRequest newComment);
}
