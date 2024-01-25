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

    @Id
    @Column(nullable = false, name = "authentication_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "user_id")
    @OneToOne
    private User user;

    @Size(max = 11)
    private String phone;

    @Size(max = 50)
    @Column(nullable = false)
    private String email;

    @Column(nullable = false) // null: No(가입불가) NECESSARY:필수동의 ALL:+선택동의
    private AgreeRange agreement;

    @Builder
    public Authentication(User user, String phone, String email, AgreeRange agreement) {
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


}
