package com.donguri.jejudorang.domain.community.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Formula;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Entity
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Community {
    @Id
    @Column(name = "community_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "writer_id")
//    private User writer;
    private Long writer;

    // 글 분류 (PARTY: 모임, CHAT: 잡담)
    @Enumerated(EnumType.STRING)
    private BoardType type;

    // 모임글인 경우 모집 상태 (RECRUITING: 모집중, DONE: 모집완료)
    @Enumerated(EnumType.STRING)
    private JoinState state;

    private String title;
    private String content;

    @ElementCollection(targetClass = String.class)
    @CollectionTable(name = "community_tags", joinColumns = @JoinColumn(name = "community_id"))
    @Column(name = "community_tag")
    private List<String> tags;

    private int viewCount;

    @OneToMany(mappedBy = "community" // 게시글(community) 1 : 여러 사용자에 의한 좋아요(Bookmark)
            , cascade = CascadeType.ALL // Board 엔티티에 대한 변경이 Bookmark 엔티티에 전파
            , orphanRemoval = true) //  Board 엔티티에서 제거된 Bookmark 엔티티가 자동으로 삭제
    private Set<Bookmark> bookmarks = new HashSet<>();

    // 페이징 정렬 위한 가상 컬럼
    @Formula("(SELECT COUNT(*) FROM bookmark b WHERE b.community_id = community_id)")
    private int bookmarksCount;


    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Builder
    public Community(Long id, Long writer, String title, String content, List<String> tags, int viewCount) {
        this.id = id;
        this.writer = writer;
        this.title = title;
        this.content = content;
        this.tags = tags;
        this.viewCount = viewCount;
    }

    // 유저 아이디 후 조건 추가 필요 ** 조회수, 모집 상태 설정
    public void upViewCount() {
        viewCount++;
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
            state = null;
        }
    }

    public void changeJoinState() {
        if (state == JoinState.RECRUITING) {
            state = JoinState.DONE;
        } else {
            state = JoinState.RECRUITING;
        }
    }

}
