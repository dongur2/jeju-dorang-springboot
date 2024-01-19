package com.donguri.jejudorang.domain.user.entity.auth;

import com.donguri.jejudorang.domain.user.entity.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class SocialLogin {

    @Id
    @Column(nullable = false)
    private String socialCode;

    @Size(max = 50)
    @Column(nullable = false)
    private String socialId;

    @Column(nullable = false)
    private String accessToken;

    @OneToOne(orphanRemoval = true)
    private User user;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;


}
