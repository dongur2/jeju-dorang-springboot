package com.donguri.jejudorang.domain.community.repository.comment;

import com.donguri.jejudorang.domain.community.entity.comment.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    Optional<List<Comment>> findAllByUserId(Long writerId);

    // 커뮤니티 상세글 댓글 조회
    List<Comment> findAllByCommunityIdOrderByCmtGroupAscCmtOrderAsc(Long communityId);
}
