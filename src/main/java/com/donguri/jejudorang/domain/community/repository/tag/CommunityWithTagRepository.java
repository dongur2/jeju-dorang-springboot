package com.donguri.jejudorang.domain.community.repository.tag;

import com.donguri.jejudorang.domain.community.entity.tag.CommunityWithTag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommunityWithTagRepository extends JpaRepository<CommunityWithTag, Long> {
}
