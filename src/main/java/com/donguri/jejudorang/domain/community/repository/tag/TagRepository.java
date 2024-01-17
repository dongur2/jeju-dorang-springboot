package com.donguri.jejudorang.domain.community.repository.tag;

import com.donguri.jejudorang.domain.community.entity.tag.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TagRepository extends JpaRepository<Tag, Long> {
    Optional<Tag> findByKeyword(String keyword);


}
