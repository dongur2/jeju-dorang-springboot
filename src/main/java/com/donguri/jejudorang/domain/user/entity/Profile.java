package com.donguri.jejudorang.domain.user.entity;

import com.donguri.jejudorang.global.common.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
public class Profile extends BaseEntity {

    @JoinColumn(nullable = false)
    @OneToOne
    private User user;

    @Size(max = 30)
    @Column(nullable = false)
    private String externalId;

    @Size(max = 15)
    @Column(nullable = false)
    private String nickname;

    private String img_url;

    @Builder
    public Profile(User user, String externalId, String nickname, String img_url) {
        this.user = user;
        this.externalId = externalId;
        this.nickname = nickname;
        this.img_url = img_url;
    }

    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }

    public void updateImg(String img_url) {
        this.img_url = img_url;
    }

}
