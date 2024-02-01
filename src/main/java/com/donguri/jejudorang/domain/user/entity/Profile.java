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

    @Id
    @Column(nullable = false, name = "profile_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "user_id")
    @OneToOne
    private User user;

    @Size(max = 20)
    @Column(nullable = false)
    private String externalId;

    @Size(max = 15)
    @Column(nullable = false)
    private String nickname;

    private String imgName;
    private String imgUrl;

    @Builder
    public Profile(User user, String externalId, String nickname, String imgName, String imgUrl) {
        this.user = user;
        this.externalId = externalId;
        this.nickname = nickname;
        this.imgName = imgName;
        this.imgUrl = imgUrl;
    }

    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }

    public void updateImgName(String imgName) { this.imgName = imgName; }

    public void updateImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

}
