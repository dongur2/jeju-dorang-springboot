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
    @Column(name = "comment_id")
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

    // * 댓글 순서 구분 ( 댓글 목록 불러올 때 이 순서대로 정렬 )
    @Column
    private Long cmtOrder;

    // * 댓글 그룹 구분 ( 댓글 + 대댓글 그룹 인덱스: 댓글 아이디 )
    @Column
    private Long cmtGroup;

    // * 댓글 깊이 표시( 대댓글 구분 ): 기본 0
    @Column(nullable = false)
    private int cmtDepth;

    // * 댓글 삭제 처리
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private IsDeleted isDeleted;


    @Builder
    public Comment(Community community, String content, User user, Long cmtOrder, Long cmtGroup, int cmtDepth, IsDeleted isDeleted) {
        this.community = community;
        this.content = content;
        this.user = user;
        this.cmtOrder = cmtOrder;
        this.cmtGroup = cmtGroup;
        this.cmtDepth = cmtDepth;
        this.isDeleted = isDeleted;
    }


    // * 댓글 그룹 설정
    public void updateCmtGroup() {
        this.cmtGroup = this.id;
    }
    public void updateCmtOrder() { this.cmtOrder = this.id; }

    // * 댓글 삭제 상태 업데이트
    public void updateIsDeleted() {
        this.isDeleted = IsDeleted.DELETED;
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
