package com.donguri.jejudorang.domain.user.entity;

import com.donguri.jejudorang.domain.user.entity.auth.Password;
import com.donguri.jejudorang.domain.user.entity.auth.SocialLogin;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class User {

    @Id
    @Column(nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING) // BASIC, KAKAO
    private LoginType loginType;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING) // ADMIN, USER
    private Role role;


    @OneToOne(fetch = FetchType.LAZY,
            mappedBy = "user", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private Profile profile;

    @OneToOne(fetch = FetchType.LAZY,
            mappedBy = "user", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private Authentication authentication;

    @OneToOne(fetch = FetchType.LAZY,
            mappedBy = "user", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private Password password;

    @OneToOne
    private SocialLogin socialLogin;


    @Builder
    public User(LoginType loginType, Role role) {
        this.loginType = loginType;
        this.role = role;
    }


    // FK setter
    public void updateProfile(Profile profile) {
        this.profile = profile;
    }

    public void updateAuthentication(Authentication authentication) {
        this.authentication = authentication;
    }

    public void updatePassword(Password password) {
        this.password = password;
    }

    public void updateSocialLogin(SocialLogin socialLogin) {
        this.socialLogin = socialLogin;
    }
}
