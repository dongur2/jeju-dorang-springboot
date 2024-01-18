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
@AllArgsConstructor
public class CommunityWithTag extends BaseEntity {

    @ManyToOne
    @JoinColumn(nullable = false)
    private Community community;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Tag tag;

}
