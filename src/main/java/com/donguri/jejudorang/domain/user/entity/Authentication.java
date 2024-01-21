package com.donguri.jejudorang.domain.user.entity;

import com.donguri.jejudorang.global.common.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
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
