package com.donguri.jejudorang.domain.board.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

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

    private String type;
    private String state;

    private String title;
    private String content;
    private String tags;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Builder
    public Board(Long writer, String type, String state, String title, String content, String tags) {
        this.writer = writer;
        this.type = type;
        this.state = state;
        this.title = title;
        this.content = content;
        this.tags = tags;
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
