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

import java.util.List;

public interface CommunityRepository extends JpaRepository<Community, Long> {

    // getPartyList, getChatList
    Page<Community> findAllByType(BoardType boardType, Pageable pageable);

    // Party List with STATE
    Page<Community> findAllByTypeAndState(BoardType boardType, JoinState state, Pageable pageable);

    // Party Search with word - no state & Chat
    @Query("select c from Community c where c.type=:type and c.title like %:word%")
    Page<Community> findAllByTypeContainingWord(@Param("type") BoardType boardType, @Param("word") String searchWord, Pageable pageable);

    // Party Search with tag - no state & Chat
    @Query("select c from Community c left outer join CommunityWithTag cwt on c.id=cwt.community.id left outer join Tag t on cwt.tag.id=t.id where c.type=:type and t.keyword in :tags group by c.id having count(c)>=:tagCount")
    Page<Community> findAllByTypeContainingTag(@Param("type") BoardType boardType, @Param("tags") List<String> tags, @Param("tagCount") int tagCount, Pageable pageable);

    // Party Search with word and tag - no state & Chat
    @Query("select c from Community c left outer join CommunityWithTag cwt on c.id=cwt.community.id left outer join Tag t on cwt.tag.id=t.id where c.type=:type and c.title like %:word% and t.keyword in :tags group by c.id having count(c)>=:tagCount")
    Page<Community> findAllByTypeContainingWordAndTag(@Param("type") BoardType boardType, @Param("word") String searchWord, @Param("tags") List<String> tags, @Param("tagCount") int tagCount, Pageable pageable);

    // Party Search with word - state
    @Query("select c from Community c where c.type=:type and c.state=:state and c.title like %:word%")
    Page<Community> findAllByTypeAndStateContainingWord(@Param("type") BoardType boardType, @Param("state") JoinState state, @Param("word") String searchWord, Pageable pageable);

    // Party Search with word and tag - state
    @Query("select c from Community c left outer join CommunityWithTag cwt on c.id=cwt.community.id left outer join Tag t on cwt.tag.id=t.id where c.type=:type and c.state=:state and c.title like %:word% and t.keyword in :tags group by c.id having count(c)>=:tagCount")
    Page<Community> findAllByTypeAndStateContainingWordAndTag(@Param("type") BoardType boardType, @Param("state") JoinState state, @Param("word") String searchWord, @Param("tags") List<String> tags, @Param("tagCount") int tagCount, Pageable pageable);

    // Party Search with tag - state
    @Query("select c from Community c left outer join CommunityWithTag cwt on c.id=cwt.community.id left outer join Tag t on cwt.tag.id=t.id where c.type=:type and c.state=:state and t.keyword in :tags group by c.id having count(c)>=:tagCount")
    Page<Community> findAllByTypeAndStateContainingTag(@Param("type") BoardType boardType, @Param("state") JoinState state, @Param("tags") List<String> tags, @Param("tagCount") int tagCount, Pageable pageable);

}
