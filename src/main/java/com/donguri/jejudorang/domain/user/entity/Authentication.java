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
public class Authentication extends BaseEntity {

    @JoinColumn(nullable = false)
    @OneToOne(orphanRemoval = true)
    private User user;

    @Size(max = 11)
    private String phone;

    @Size(max = 50)
    @Column(nullable = false)
    private String email;

    @Column(nullable = false) // 0: No(가입불가) 1:필수동의 2:+선택동의
    private byte agreement;

    @Builder
    public Authentication(User user, String phone, String email, byte agreement) {
        this.user = user;
        this.phone = phone;
        this.email = email;
        this.agreement = agreement;
    }

    public void updatePhone(String phone) {
        this.phone = phone;
    }

    public void updateEmail(String email) {
        this.email = email;
    }

    public void updateAgreement(byte no) {
        this.agreement = no;
    }
}
