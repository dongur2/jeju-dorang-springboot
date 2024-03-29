package com.donguri.jejudorang.domain.community.entity;

import com.donguri.jejudorang.domain.community.dto.request.CommunityWriteRequest;
import com.donguri.jejudorang.domain.bookmark.entity.CommunityBookmark;
import com.donguri.jejudorang.domain.community.entity.comment.Comment;
import com.donguri.jejudorang.domain.community.entity.tag.CommunityWithTag;
import com.donguri.jejudorang.domain.user.entity.User;
import com.donguri.jejudorang.global.common.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Formula;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Entity
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Community extends BaseEntity {

    @Id
    @Column(name = "community_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) // 한 사람 당 게시글 여러 개
    @JoinColumn(name = "user_id") // 회원 탈퇴 -> 삭제되지 않고 NULL 전환
    private User writer;

    // 글 분류 (PARTY: 모임, CHAT: 잡담)
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private BoardType type;

    // 모임글인 경우 모집 상태 (RECRUITING: 모집중, DONE: 모집완료, NOT_PARTY: 잡담글)
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private JoinState state;

    @Size(max = 60)
    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @OneToMany(mappedBy = "community", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CommunityWithTag> tags;

    @ColumnDefault("0")
    @Column(nullable = false)
    private int viewCount;

    @OneToMany(mappedBy = "community" // 게시글(community) 1 : 여러 사용자에 의한 북마크(Bookmark)
            , cascade = CascadeType.PERSIST)
    private Set<CommunityBookmark> bookmarks = new HashSet<>();

    // 페이징 정렬 위한 가상 컬럼
    @Formula("(SELECT COUNT(*) FROM community_bookmark b WHERE b.community_id = community_id)")
    private int bookmarkCount;

    @OneToMany(mappedBy = "community"
            , cascade = CascadeType.ALL
            , orphanRemoval = true)
    private List<Comment> comments;

    @Formula("(SELECT COUNT(*) FROM comment c WHERE c.community_id = community_id AND c.is_deleted = 'EXISTING')")
    private int commentCount;


    @Builder
    public Community(User writer, String title, String content, List<CommunityWithTag> tags, int viewCount, List<Comment> comments, int commentCount) {
        this.writer = writer;
        this.title = title;
        this.content = content;
        this.tags = tags;
        this.viewCount = viewCount;
        this.comments = comments;
        this.commentCount = commentCount;
    }

    // 유저 아이디 후 조건 추가 필요 ** 조회수, 모집 상태 설정
    public void upViewCount() {
        viewCount++;
    }

    // 글 수정
    public void update(CommunityWriteRequest resource) {
        title = resource.title();
        content = resource.content();

        setBoardType(resource.type());
        setDefaultJoinState();
    }

    // 북마크 업데이트
    public void updateBookmarks(CommunityBookmark bookmark) {
        if (bookmarks.contains(bookmark)) {
            bookmarks.remove(bookmark);
        } else {
            bookmarks.add(bookmark);
        }
    }

    // 북마크 삭제
    public void deleteBookmark(CommunityBookmark bookmark) {
        bookmarks.remove(bookmark);
    }

    // 댓글 업데이트
    public void addComment(Comment newComment) {
        if(comments == null) {
            comments = new ArrayList<>();
        }
        comments.add(newComment);
    }

    public void setBoardType(String paramType) {
        if (paramType.equals("chat")) {
            type = BoardType.CHAT;
        } else {
            type = BoardType.PARTY;
        }
    }

    public void setDefaultJoinState() {
        if (type.equals(BoardType.PARTY) && state != JoinState.DONE) {
            state = JoinState.RECRUITING;
        } else if (type.equals(BoardType.CHAT)) {
            state = JoinState.NOT_PARTY;
        }
    }

    public void changeJoinState() {
        if (state == JoinState.RECRUITING) {
            state = JoinState.DONE;
        } else {
            state = JoinState.RECRUITING;
        }
    }

    public void deleteWriter() {
        writer = null;
    }

}
