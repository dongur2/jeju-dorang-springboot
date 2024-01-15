package com.donguri.jejudorang.domain.community.repository;

import com.donguri.jejudorang.domain.community.entity.Community;
import com.donguri.jejudorang.domain.community.entity.BoardType;
import com.donguri.jejudorang.domain.community.entity.JoinState;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommunityRepository extends JpaRepository<Community, Long> {

    // getPartyList, getChatList
    Page<Community> findAllByType(BoardType boardType, Pageable pageable);
    Page<Community> findAllByTypeAndState(BoardType boardType, JoinState state, Pageable pageable);
}
