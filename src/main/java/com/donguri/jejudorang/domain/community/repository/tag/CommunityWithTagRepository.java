package com.donguri.jejudorang.domain.community.repository.tag;

import com.donguri.jejudorang.domain.community.entity.Community;
import com.donguri.jejudorang.domain.community.entity.tag.CommunityWithTag;
import com.donguri.jejudorang.domain.community.entity.tag.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommunityWithTagRepository extends JpaRepository<CommunityWithTag, Long> {
    Optional<CommunityWithTag> findByCommunityAndTag(Community community, Tag tag);
}
