package com.donguri.jejudorang.domain.user.entity;

import com.donguri.jejudorang.domain.community.entity.bookmark.Bookmark;
import com.donguri.jejudorang.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Set;

@Entity
@Getter
@NoArgsConstructor
public class User extends BaseEntity {

    private String email;
    private String nickname;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Bookmark> boardLiked;

    @Builder
    public User(String email, String nickname) {
        this.email = email;
        this.nickname = nickname;
    }

    @Override
    public String toString() {
        return "User{" +
                ", email='" + email + '\'' +
                ", nickname='" + nickname + '\'' +
                ", boardLiked=" + boardLiked +
                '}';
    }
}
