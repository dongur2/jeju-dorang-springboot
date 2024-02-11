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
    @JoinColumn(name = "writer_id")
    private User user; // 회원 탈퇴해도 삭제되지 않음


    @Builder
    public Comment(Community community, String content, User user) {
        this.community = community;
        this.content = content;
        this.user = user;
    }

    // 댓글 내용 수정
    public void updateContent(String content) {
        this.content = content;
    }

    // 댓글 작성자 NULL 처리 - 작성자 탈퇴
    public void deleteWriter() {
        this.user = null;
    }
}
