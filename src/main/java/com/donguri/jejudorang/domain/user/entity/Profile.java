package com.donguri.jejudorang.domain.user.entity;

import com.donguri.jejudorang.global.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Profile extends BaseEntity {

    @JoinColumn(nullable = false)
    @OneToOne(orphanRemoval = true)
    private User user;

    @Size(max = 15)
    @Column(nullable = false)
    private String nickname;

    private String img_url;

}
