package com.donguri.jejudorang.domain.community.entity.tag;

import com.donguri.jejudorang.global.common.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Entity
@NoArgsConstructor
public class Tag extends BaseEntity {

    @Id
    @Column(nullable = false, name = "tag_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Size(max = 20)
    @Column(nullable = false)
    private String keyword;

    @OneToMany(mappedBy = "tag", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CommunityWithTag> postsWithTag;


    @Builder
    public Tag(String keyword, List<CommunityWithTag> postsWithTag) {
        this.keyword = keyword;
        this.postsWithTag = postsWithTag;
    }

}
