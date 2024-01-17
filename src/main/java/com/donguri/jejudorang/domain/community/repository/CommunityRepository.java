package com.donguri.jejudorang.domain.community.repository;

import com.donguri.jejudorang.domain.community.entity.Community;
import com.donguri.jejudorang.domain.community.entity.BoardType;
import com.donguri.jejudorang.domain.community.entity.JoinState;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;

public interface CommunityRepository extends JpaRepository<Community, Long> {

    // getPartyList, getChatList
    Page<Community> findAllByType(BoardType boardType, Pageable pageable);

    // Party List with STATE
    Page<Community> findAllByTypeAndState(BoardType boardType, JoinState state, Pageable pageable);

    // Party Search with word - no state
    @Query("select c from Community c where c.type=:type and c.title like %:word%")
    Page<Community> findAllPartiesWithSearchWord(@Param("type") BoardType boardType, @Param("word") String searchWord, Pageable pageable);

    // Party Search with word - state
    @Query("select c from Community c where c.type=:type and c.state=:state and c.title like %:word%")
    Page<Community> findAllPartiesWithTypeAndSearchWord(@Param("type") BoardType boardType, @Param("state") JoinState state, @Param("word") String searchWord, Pageable pageable);



    // Chat Search with word
    @Query("select c from Community c where c.type=:type and c.title like %:word%")
    Page<Community> findAllChatsWithSearchWord(@Param("type") BoardType boardType, @Param("word") String searchWord, Pageable pageable);


}
