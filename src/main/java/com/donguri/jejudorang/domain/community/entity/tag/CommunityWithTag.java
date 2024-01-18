package com.donguri.jejudorang.domain.community.entity.tag;

import com.donguri.jejudorang.domain.community.entity.Community;
import com.donguri.jejudorang.global.common.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
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
    private Community community;

    @ManyToOne
    private Tag tag;

}
