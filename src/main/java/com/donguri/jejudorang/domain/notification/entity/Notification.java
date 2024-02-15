package com.donguri.jejudorang.domain.notification.entity;

import com.donguri.jejudorang.domain.user.entity.User;
import com.donguri.jejudorang.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
public class Notification extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(nullable = false, name = "user_id")
    User owner; // 회원 탈퇴시 함께 삭제

    @Column(nullable = false)
    String content;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    IsChecked isChecked; // 읽음 상태

    @Builder
    public Notification(User owner, String content, IsChecked isChecked) {
        this.owner = owner;
        this.content = content;
        this.isChecked = isChecked;
    }

}
