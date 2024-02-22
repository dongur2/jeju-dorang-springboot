package com.donguri.jejudorang.domain.user.entity;

import com.donguri.jejudorang.domain.user.entity.auth.Authentication;
import com.donguri.jejudorang.domain.user.entity.auth.Password;
import com.donguri.jejudorang.domain.user.entity.auth.SocialLogin;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@NoArgsConstructor
public class User {

    @Id
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING) // BASIC, KAKAO
    private LoginType loginType;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable( name = "user_roles",
                joinColumns = @JoinColumn(name = "user_id"),
                inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();


    @OneToOne(fetch = FetchType.LAZY,
            mappedBy = "user", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private Profile profile;

    @OneToOne(fetch = FetchType.LAZY,
            mappedBy = "user", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private Authentication auth;

    @OneToOne(fetch = FetchType.LAZY,
            mappedBy = "user", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private Password pwd;

    @OneToOne(fetch = FetchType.LAZY,
            mappedBy = "user", cascade = CascadeType.PERSIST, orphanRemoval = true)
    @JoinColumn(name = "social_login_code")
    private SocialLogin socialLogin;


    public User(LoginType loginType) {
        this.loginType = loginType;
    }

    @Builder
    public User(LoginType loginType, Set<Role> roles, Profile profile, Authentication auth, Password pwd) {
        this.loginType = loginType;
        this.roles = roles;
        this.profile = profile;
        this.auth = auth;
        this.pwd = pwd;
    }

    public User(User user) {
        loginType = user.getLoginType();
        roles = user.getRoles();
        profile = user.getProfile();
        auth = user.getAuth();
        pwd = user.getPwd();
        socialLogin = user.getSocialLogin();
    }



    // role setter
    public void updateRole(Set<Role> roles) {
        this.roles = roles;
    }

    // FK setter
    public void updateProfile(Profile profile) {
        this.profile = profile;
    }

    public void updateAuth(Authentication authentication) {
        this.auth = authentication;
    }

    public void updatePwd(Password password) {
        this.pwd = password;
    }

    public void updateSocialLogin(SocialLogin socialLogin) {
        this.socialLogin = socialLogin;
    }
}
