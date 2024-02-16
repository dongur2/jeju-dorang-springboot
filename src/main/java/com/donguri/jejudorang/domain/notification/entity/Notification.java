package com.donguri.jejudorang.domain.notification.entity;

import com.donguri.jejudorang.domain.community.entity.Community;
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

    @ManyToOne
    @JoinColumn(nullable = false, name = "user_id")
    User owner; // 회원 탈퇴시 함께 삭제

    @ManyToOne
    @JoinColumn(nullable = false, name = "community_id")
    Community post;

    @Column(nullable = false)
    String content;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    IsChecked isChecked; // 읽음 상태: 알림을 통해 게시글로 이동할 때만 변경

    @Builder
    public Notification(User owner, Community post, String content, IsChecked isChecked) {
        this.owner = owner;
        this.post = post;
        this.content = content;
        this.isChecked = isChecked;
    }


    // 읽음 상태 업데이트
    public void updateIsChecked() {
        this.isChecked = IsChecked.CHECKED;
    }

}
