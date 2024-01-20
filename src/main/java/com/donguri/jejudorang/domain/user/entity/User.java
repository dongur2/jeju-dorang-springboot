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
    @Column(nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long num;

    @Column(nullable = false)
    @ColumnDefault(value = "1") // 0: admin, 1: basic, 2: kakao
    private byte loginType;

    @Builder
    public User(byte loginType) {
        this.loginType = loginType;
    }
}
