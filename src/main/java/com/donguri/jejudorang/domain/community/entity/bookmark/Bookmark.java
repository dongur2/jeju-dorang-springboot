package com.donguri.jejudorang.domain.community.entity.bookmark;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "community_id")
    private Community community;

    @Builder
    public Bookmark(User user, Community community) {
        this.user = user;
        this.community = community;
    }
}
