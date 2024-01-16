package com.donguri.jejudorang.domain.community.repository;

import com.donguri.jejudorang.domain.community.entity.Community;
import com.donguri.jejudorang.domain.community.entity.BoardType;
import com.donguri.jejudorang.domain.community.entity.JoinState;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CommunityRepository extends JpaRepository<Community, Long> {

    // getPartyList, getChatList
    Page<Community> findAllByType(BoardType boardType, Pageable pageable);
    Page<Community> findAllByTypeAndState(BoardType boardType, JoinState state, Pageable pageable);


    // Chat Search
    @Query("select c from Community c where c.type=:type and c.title like %:word%")
    Page<Community> findAllChatsWithSearchWord(@Param("type") BoardType boardType, @Param("word") String searchWord, Pageable pageable);

}
