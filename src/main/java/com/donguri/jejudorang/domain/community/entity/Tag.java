package com.donguri.jejudorang.domain.community.entity;

import com.donguri.jejudorang.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Entity
@NoArgsConstructor
public class Tag extends BaseEntity {

    private String keyword;

    @OneToMany(mappedBy = "tag", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Community> postsWithTag;

    @Builder
    public Tag(String keyword, List<Community> postsWithTag) {
        this.keyword = keyword;
        this.postsWithTag = postsWithTag;
    }
}
