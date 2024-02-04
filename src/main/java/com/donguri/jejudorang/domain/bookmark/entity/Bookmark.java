package com.donguri.jejudorang.domain.bookmark.entity;

import com.donguri.jejudorang.domain.community.entity.Community;
import com.donguri.jejudorang.domain.user.entity.User;
import com.donguri.jejudorang.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Bookmark extends BaseEntity {

    @Id
    @Column(nullable = false, name = "bookmark_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(nullable = false, name = "user_id")
    @ManyToOne
    private User user;

    @JoinColumn(nullable = false, name = "community_id")
    @ManyToOne
    private Community community;

    @Builder
    public Bookmark(User user, Community community) {
        this.user = user;
        this.community = community;
    }
}
