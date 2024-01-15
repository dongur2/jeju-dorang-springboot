package com.donguri.jejudorang.domain.board.entity;

import com.donguri.jejudorang.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
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
public class Board {
    @Id
    @Column(name = "board_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "writer_id")
//    private User writer;
    private Long writer;

    @Enumerated(EnumType.STRING)
    private BoardType type;

    // 모임글인 경우 모집 상태 (RECRUITING: 모집중, DONE: 모집완료)
    @Enumerated(EnumType.STRING)
    private JoinState state;

    private String title;
    private String content;

    @ElementCollection(targetClass = String.class)
    @CollectionTable(name = "board_tags", joinColumns = @JoinColumn(name = "board_id"))
    @Column(name = "board_tag")
    private List<String> tags;

    private int viewCount;

    @OneToMany(mappedBy = "board" // 게시글(Board) 1 : 여러 사용자에 의한 좋아요(Liked)
            , cascade = CascadeType.ALL // Board 엔티티에 대한 변경이 Liked 엔티티에 전파
            , orphanRemoval = true) //  Board 엔티티에서 제거된 Liked 엔티티가 자동으로 삭제
    private Set<Liked> liked = new HashSet<>();

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Builder
    public Board(Long id, Long writer, String title, String content, List<String> tags, int viewCount) {
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



    @Override
    public String toString() {
        return "Board{" +
                "id=" + id +
                ", writer=" + writer +
                ", type='" + type + '\'' +
                ", state='" + state + '\'' +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", tags='" + tags + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
