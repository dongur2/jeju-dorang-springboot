package com.donguri.jejudorang.domain.user.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Getter
@NoArgsConstructor
public class User {

    @Id
    @Column(name = "user_no", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long num;

    @Column(nullable = false)
    @ColumnDefault(value = "0") // 0: basic, 1: kakao
    private byte loginType;

    @Builder
    public User(Long num, byte loginType) {
        this.num = num;
        this.loginType = loginType;
    }
}
