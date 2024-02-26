package com.donguri.jejudorang.domain.user.entity.auth;

import com.donguri.jejudorang.domain.user.entity.AgreeRange;
import com.donguri.jejudorang.domain.user.entity.User;
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

    @Size(max = 80)
    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING) // ALL, NECESSARY
    private AgreeRange agreement;

    @Builder
    public Authentication(User user, String email, AgreeRange agreement) {
        this.user = user;
        this.email = email;
        this.agreement = agreement;
    }

    public void updateEmail(String email) {
        this.email = email;
    }


}
