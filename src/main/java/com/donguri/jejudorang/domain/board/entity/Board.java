package com.donguri.jejudorang.domain.board.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;

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

    @Enumerated(EnumType.STRING)
    private JoinState joining;

    private String title;
    private String content;

    @ElementCollection(targetClass = String.class)
    @CollectionTable(name = "board_tags", joinColumns = @JoinColumn(name = "board_id"))
    @Column(name = "board_tag")
    private List<String> tags;

    private int viewCount;
    private int likedCount;

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
        if (type.equals(BoardType.PARTY) && joining != JoinState.DONE) {
            joining = JoinState.FINDING;
        } else if (type.equals(BoardType.CHAT)) {
            joining = null;
        }
    }

    public void changeJoinState() {
        if (joining == JoinState.FINDING) {
            joining = JoinState.DONE;
        } else {
            joining = JoinState.FINDING;
        }
    }



    @Override
    public String toString() {
        return "Board{" +
                "id=" + id +
                ", writer=" + writer +
                ", type='" + type + '\'' +
                ", state='" + joining + '\'' +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", tags='" + tags + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
