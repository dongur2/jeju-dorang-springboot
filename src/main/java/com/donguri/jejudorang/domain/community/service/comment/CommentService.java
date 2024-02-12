package com.donguri.jejudorang.domain.community.service.comment;

import com.donguri.jejudorang.domain.community.dto.request.comment.CommentRequest;
import com.donguri.jejudorang.domain.community.dto.request.comment.CommentRequestWithId;
import com.donguri.jejudorang.domain.community.dto.response.comment.CommentResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface CommentService {

    // 새 댓글 작성
    void writeNewComment(String accessToken, Long postId, CommentRequest newComment);

    // 댓글 수정
    void modifyComment(String accessToken, CommentRequestWithId commentToUpdate) throws IllegalAccessException;

    // 댓글 삭제
    void deleteComment(String accessToken, Long cmtId) throws IllegalAccessException;

    // 회원 탈퇴시 탈퇴 회원이 작성한 댓글 연관관계 삭제
    void findAllCmtsByUserAndSetWriterNull(Long writerId);


    // 커뮤니티 상세글 조회: 댓글 조회
    List<CommentResponse> findAllCmtsOnCommunity(Long communityId);

}
