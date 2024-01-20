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

    @JoinColumn(nullable = false)
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE) // user 삭제 -> bookmark 삭제
    private User user;

    @JoinColumn(nullable = false)
    @ManyToOne(cascade = CascadeType.REMOVE) // community 삭제 -> bookmark 삭제
    private Community community;

    @Builder
    public Bookmark(User user, Community community) {
        this.user = user;
        this.community = community;
    }
}
