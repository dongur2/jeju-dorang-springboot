package com.donguri.jejudorang.domain.community.entity.tag;

import com.donguri.jejudorang.domain.community.entity.Community;
import com.donguri.jejudorang.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(
        name = "community_with_tag",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "unique_map",
                        columnNames = {
                                "community_id",
                                "tag_id"
                        }
                )
        }
)
@NoArgsConstructor
public class CommunityWithTag extends BaseEntity {

    @Id
    @Column(nullable = false, name = "community_with_tag_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(nullable = false, name = "community_id")
    private Community community;

    @ManyToOne
    @JoinColumn(nullable = false, name = "tag_id")
    private Tag tag;

    public CommunityWithTag(Community community, Tag tag) {
        this.community = community;
        this.tag = tag;
    }
}
