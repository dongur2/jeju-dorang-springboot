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
public class CommunityBookmark extends BaseEntity {

    @Id
    @Column(name = "community_bookmark_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "community_id")
    private Community community;

    @Builder
    public CommunityBookmark(User user, Community community) {
        this.user = user;
        this.community = community;
    }


    public void updateCommunityWhenDeleted() {
        this.community = null;
    }
}
