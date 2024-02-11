package com.donguri.jejudorang.domain.community.entity.comment;

import com.donguri.jejudorang.domain.community.dto.request.comment.CommentRequest;
import com.donguri.jejudorang.domain.community.entity.Community;
import com.donguri.jejudorang.domain.user.entity.User;
import com.donguri.jejudorang.global.common.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
public class Comment extends BaseEntity {

    @Id
    @Column(nullable = false, name = "comment_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "community_id")
    private Community community; // 게시글이 삭제되면 함께 삭제

    @Size(max = 50)
    @Column(nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "writer_id")
    private User user; // 회원 탈퇴하면 함께 삭제


    @Builder
    public Comment(Community community, String content, User user) {
        this.community = community;
        this.content = content;
        this.user = user;
    }

    // 댓글 내용 수정
    public void updateContent(CommentRequest commentRequest) {
        this.content = commentRequest.content();
    }
}
