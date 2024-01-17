package com.donguri.jejudorang.domain.community.entity.tag;

import com.donguri.jejudorang.domain.community.entity.Community;
import com.donguri.jejudorang.global.common.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class CommunityWithTag extends BaseEntity {

    @ManyToOne
    private Community community;

    @ManyToOne
    private Tag tag;

}
