package com.donguri.jejudorang.domain.community.entity;

import com.donguri.jejudorang.domain.community.dto.request.CommunityWriteRequestDto;
import com.donguri.jejudorang.domain.bookmark.entity.Bookmark;
import com.donguri.jejudorang.domain.community.entity.tag.CommunityWithTag;
import com.donguri.jejudorang.global.common.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Formula;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Entity
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Community extends BaseEntity {

//    * 작성자 임시 코드
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "writer_id")
//    @Column(nullable = false)
//    private User writer;
    private Long writer;

    // 글 분류 (PARTY: 모임, CHAT: 잡담)
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private BoardType type;

    // 모임글인 경우 모집 상태 (RECRUITING: 모집중, DONE: 모집완료)
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

    @OneToMany(mappedBy = "community" // 게시글(community) 1 : 여러 사용자에 의한 좋아요(Bookmark)
            , cascade = CascadeType.ALL // Community 엔티티에 대한 변경이 Bookmark 엔티티에 전파
            , orphanRemoval = true) //  Community 엔티티에서 제거된 Bookmark 엔티티가 자동으로 삭제
    private Set<Bookmark> bookmarks = new HashSet<>();

    // 페이징 정렬 위한 가상 컬럼
    @Formula("(SELECT COUNT(*) FROM bookmark b WHERE b.id = id)")
    private int bookmarksCount;


    @Builder
    public Community(Long writer, String title, String content, List<CommunityWithTag> tags, int viewCount) {
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

    // 글 수정
    public void update(CommunityWriteRequestDto resource) {
        title = resource.title();
        content = resource.content();

        setBoardType(resource.type());
        setDefaultJoinState();
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
