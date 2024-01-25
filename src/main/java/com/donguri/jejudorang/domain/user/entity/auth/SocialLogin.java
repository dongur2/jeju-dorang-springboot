package com.donguri.jejudorang.domain.user.entity.auth;

import com.donguri.jejudorang.domain.user.entity.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor
public class SocialLogin {

    @Id
    @Column(nullable = false)
    private String socialCode;

    @Size(max = 50)
    @Column(nullable = false)
    private String socialId;

    @Column(nullable = false)
    private String accessToken;

    @JoinColumn(name = "user_id")
    @OneToOne(orphanRemoval = true)
    private User user;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Builder
    public SocialLogin(String socialCode, String socialId, String accessToken, User user) {
        this.socialCode = socialCode;
        this.socialId = socialId;
        this.accessToken = accessToken;
        this.user = user;
    }
}
