package com.donguri.jejudorang.domain.community.repository.comment;

import com.donguri.jejudorang.domain.community.entity.comment.ReComment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReCommentRepository extends JpaRepository<ReComment, Long> {
}
