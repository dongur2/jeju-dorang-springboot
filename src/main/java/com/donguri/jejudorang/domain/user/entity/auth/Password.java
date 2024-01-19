package com.donguri.jejudorang.domain.user.entity.auth;

import com.donguri.jejudorang.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Password {

    @Id
    @Column(nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(nullable = false)
    @OneToOne(orphanRemoval = true)
    private User user;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String salt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

}
