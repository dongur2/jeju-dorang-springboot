package com.donguri.jejudorang.domain.community.repository.comment;

import com.donguri.jejudorang.domain.community.entity.comment.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
