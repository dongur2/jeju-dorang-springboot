package com.donguri.jejudorang.domain.community.entity.comment;

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
public class ReComment extends BaseEntity {

    @Id
    @Column(nullable = false, name = "recomment_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "community_id")
    private Community community; // 게시글이 삭제되면 함께 삭제

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id")
    private Comment comment; // 댓글 삭제해도 대댓글은 삭제되지 않음

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "writer_id")
    private User user; // 회원 탈퇴해도 삭제되지 않음

    @Size(max = 50)
    @Column(nullable = false)
    private String content;

    @Builder
    public ReComment(Community community, Comment comment, User user, String content) {
        this.community = community;
        this.comment = comment;
        this.user = user;
        this.content = content;
    }


    // 내용 수정
    public void updateContent(String content) {
        this.content = content;
    }
}
