package com.donguri.jejudorang.domain.bookmark.repository;

import com.donguri.jejudorang.domain.bookmark.entity.Bookmark;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
    Optional<Bookmark> findByUserIdAndCommunityId(Long UserId, Long CommunityId);
}
