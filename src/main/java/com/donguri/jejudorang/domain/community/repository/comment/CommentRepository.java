package com.donguri.jejudorang.domain.community.repository.comment;

import com.donguri.jejudorang.domain.community.entity.comment.Comment;
import com.donguri.jejudorang.domain.community.entity.comment.IsDeleted;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    Optional<List<Comment>> findAllByUserId(Long writerId);

    // 커뮤니티 상세글 댓글 조회
    List<Comment> findAllByCommunityIdAndIsDeletedOrderByCmtGroupAscCmtOrderAsc(Long communityId, IsDeleted isDeleted);

    // 댓글에 포함된 삭제되지 않은 대댓글
    List<Comment> findAllByCmtGroupAndCmtDepthAndIsDeleted(int cmtDepth, Long commentId, IsDeleted isDeleted);

    // 댓글 + 댓글에 해당하는 대댓글 모두 삭제
    void deleteAllByCmtGroup(Long commentId);
}
