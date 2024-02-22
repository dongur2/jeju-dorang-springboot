package com.donguri.jejudorang.domain.user.entity.auth;

import com.donguri.jejudorang.domain.user.entity.User;
import com.donguri.jejudorang.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
public class SocialLogin extends BaseEntity {

    @Id
    @Column(nullable = false)
    private String socialCode;

    @Column(nullable = false)
    private String socialExternalId;

    @JoinColumn(name = "user_id", nullable = false)
    @OneToOne
    private User user;

    @Builder
    public SocialLogin(String socialCode, String socialExternalId, User user) {
        this.socialCode = socialCode;
        this.socialExternalId = socialExternalId;
        this.user = user;
    }
}
