package com.donguri.jejudorang.domain.user.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;



@Entity
@Getter
@NoArgsConstructor
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private Long id;

    @Size(max = 10)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ERole name;


    public Role(ERole name) {
        this.name = name;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(ERole name) {
        this.name = name;
    }

}


